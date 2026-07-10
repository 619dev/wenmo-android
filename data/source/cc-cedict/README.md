# CC-CEDICT source record

Source: https://cc-cedict.org/editor/editor.php?handler=Download

Retrieved: 2026-07-10

License: Creative Commons Attribution-ShareAlike 4.0 International
https://creativecommons.org/licenses/by-sa/4.0/

The upstream archive contains `cedict_ts.u8`. Run:

```sh
cargo run --manifest-path engine/Cargo.toml --bin compile_cedict -- \
  data/source/cc-cedict/cedict_ts.u8 app/src/main/assets/cedict_pinyin.tsv \
  data/source/unihan/Unihan_DictionaryLikeData.txt
```

The generated index is a derivative database and remains available under CC BY-SA 4.0.
