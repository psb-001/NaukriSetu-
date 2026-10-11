-- NaukriSetu initial schema — mirrors the in-app DemoStore/Seed model.
-- profiles: one row per seeker/employer/verifier (keyed by login email).
-- opportunities: job postings; status Live|Pending|Rejected.
--   criteria is jsonb: [{"type":"City","city":"Pune"},
--     {"type":"Certificate","name":"..."}, {"type":"MinAge","years":18},
--     {"type":"MinExperience","years":3}]
-- courses: static upskilling catalog.
-- kyc_submissions: verifier queue for KYC approvals.

create table if not exists public.profiles (
  id uuid primary key default gen_random_uuid(),
  email text unique not null,
  name text not null default '',
  phone text not null default '',
  city text not null default '',
  age int,
  experience_years int,
  skills text[] not null default '{}',
  certificates text[] not null default '{}',
  role text not null default 'seeker',
  goal text,
  pay_minimum int,
  aadhaar text,
  address_proof boolean not null default false,
  kyc_status text not null default 'none',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.opportunities (
  id text primary key,
  title text not null,
  employer text not null default '',
  city text not null default '',
  pay_min int not null default 0,
  pay_max int not null default 0,
  distance_km double precision not null default 0,
  required_skills text[] not null default '{}',
  criteria jsonb not null default '[]',
  status text not null default 'Live',
  description text not null default '',
  work_involved text not null default '',
  employer_id text not null default '',
  created_at timestamptz not null default now()
);

create table if not exists public.courses (
  code text primary key,
  title text not null,
  provider text not null default '',
  weeks int not null default 0,
  skills_taught text[] not null default '{}'
);

create table if not exists public.kyc_submissions (
  id uuid primary key default gen_random_uuid(),
  seeker_email text not null,
  aadhaar text not null default '',
  address_proof boolean not null default false,
  certificates text[] not null default '{}',
  status text not null default 'pending',
  created_at timestamptz not null default now()
);

-- Demo RLS: app uses the anon key with demo-local login (any @gmail + OTP 1234),
-- so allow anon full access. Tighten to auth.uid() policies when real Auth lands.
alter table public.profiles enable row level security;
alter table public.opportunities enable row level security;
alter table public.courses enable row level security;
alter table public.kyc_submissions enable row level security;

drop policy if exists "demo all" on public.profiles;
create policy "demo all" on public.profiles for all to anon using (true) with check (true);
drop policy if exists "demo all" on public.opportunities;
create policy "demo all" on public.opportunities for all to anon using (true) with check (true);
drop policy if exists "demo all" on public.courses;
create policy "demo all" on public.courses for all to anon using (true) with check (true);
drop policy if exists "demo all" on public.kyc_submissions;
create policy "demo all" on public.kyc_submissions for all to anon using (true) with check (true);
