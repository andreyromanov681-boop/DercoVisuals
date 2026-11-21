from pathlib import Path

ROOT = Path(__file__).resolve().parent
parts = sorted(ROOT.glob("generate_derco_assets.py.part*"))
if len(parts) != 3:
    raise SystemExit(f"Expected 3 source parts for DercoVisuals artwork, found {len(parts)}")
source = "".join(part.read_text(encoding="utf-8") for part in parts)
namespace = {"__name__": "__main__", "__file__": str(Path(__file__).resolve())}
exec(compile(source, str(Path(__file__).resolve()), "exec"), namespace)
