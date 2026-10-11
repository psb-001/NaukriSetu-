"""Convert Kombai canvas SVGs (pure filled paths) to Android VectorDrawables.

Strips c2pa metadata, drops the white background rect, maps rgb() fills to
#RRGGBB, keeps path data verbatim (Android supports the same M/L/C/Q/Z ops).
Idempotent: re-running overwrites identical output.
"""
import re
import sys
import os

SRC_DIR = "C:/Users/Asus/.kombai-assets"  # overridden by argv in practice
JOBS = [
    # (source svg, viewBox w/h, dest name)
]


def rgb_to_hex(fill: str) -> str:
    m = re.match(r"rgb\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)", fill)
    if m:
        return "#%02X%02X%02X" % tuple(int(x) for x in m.groups())
    m = re.match(r"#[0-9a-fA-F]{6}$", fill.strip())
    if m:
        return fill.strip().upper()
    raise ValueError(f"unsupported fill: {fill}")


def convert(src: str, name: str) -> str:
    raw = open(src, encoding="utf-8").read()
    body = re.sub(r"<metadata>.*?</metadata>", "", raw, flags=re.S)
    vb = re.search(r'viewBox="([\d.\s-]+)"', raw)
    if not vb:
        raise ValueError(f"no viewBox in {src}")
    vw, vh = vb.group(1).split()[2:4]
    paths = re.findall(r"<path[^>]*>", body)
    out = [
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
        f'    android:width="{vw}dp"',
        f'    android:height="{vh}dp"',
        f'    android:viewportWidth="{vw}"',
        f'    android:viewportHeight="{vh}">',
    ]
    kept = 0
    for i, tag in enumerate(paths):
        d = re.search(r'd="([^"]+)"', tag)
        f = re.search(r'fill="([^"]+)"', tag)
        if not d or not f:
            continue
        hexfill = rgb_to_hex(f.group(1))
        # Drop the full-canvas white background rect (first path, white fill).
        if i == 0 and hexfill == "#FFFFFF":
            continue
        data = d.group(1).replace("&quot;", '"').replace("&amp;", "&")
        out.append(f'    <path android:fillColor="{hexfill}" android:pathData="{data}"/>')
        kept += 1
    out.append("</vector>")
    print(f"{name}: {len(paths)} svg paths -> {kept} vector paths")
    return "\n".join(out) + "\n"


def main() -> None:
    pairs = [
        (".kombai/canvas/assets/splash-bridge.svg", "ill_splash_bridge.xml"),
        (".kombai/canvas/assets/banner-ac-intake.svg", "ill_banner_ac.xml"),
        (".kombai/canvas/assets/empty-search.svg", "ill_empty_search.xml"),
        (".kombai/canvas/assets/kyc-shield.svg", "ill_kyc_shield.xml"),
    ]
    dest_dir = "app/src/main/res/drawable"
    os.makedirs(dest_dir, exist_ok=True)
    for src, name in pairs:
        xml = convert(src, name)
        with open(os.path.join(dest_dir, name), "w", encoding="utf-8") as fh:
            fh.write(xml)


if __name__ == "__main__":
    main()
