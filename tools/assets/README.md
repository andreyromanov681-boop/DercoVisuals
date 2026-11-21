# DercoVisuals original visual assets

The art source in this directory is an original, procedural Python/Pillow illustration set.
It produces transparent electric-blue/cyan pictograms and the DercoVisuals wordmark; it does
not bundle third-party logos or copied client artwork.

## Regenerate

```sh
python3 -m pip install -r tools/assets/requirements.txt
python3 tools/assets/generate_derco_assets.py
```

The generator writes 42 PNGs plus `asset-manifest.json` into `tools/assets/png/`.
`tools/prepare_pulse_foundation.py` validates the manifest, copies those PNGs into
`src/main/resources/assets/dercovisuals/`, then checks every registered texture path.

The split `generate_derco_assets.py.partNN` text files are concatenated by the small
entrypoint script; together they are the complete editable artwork source.
