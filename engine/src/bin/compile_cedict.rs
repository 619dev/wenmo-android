use std::collections::{BTreeMap, HashMap, HashSet};
use std::env;
use std::fs::File;
use std::io::{BufRead, BufReader, BufWriter, Write};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let mut args = env::args().skip(1);
    let input = args.next().ok_or("usage: compile_cedict INPUT OUTPUT")?;
    let output = args.next().ok_or("usage: compile_cedict INPUT OUTPUT")?;
    let priorities = args.next().map(load_unihan_grade_level).transpose()?.unwrap_or_default();
    let reader = BufReader::new(File::open(input)?);
    let mut entries: BTreeMap<String, Vec<(String, String)>> = BTreeMap::new();

    for line in reader.lines() {
        let line = line?;
        if line.starts_with('#') || line.trim().is_empty() { continue; }
        let Some(open) = line.find('[') else { continue };
        let Some(close_relative) = line[open + 1..].find(']') else { continue };
        let close = open + 1 + close_relative;
        let mut words = line[..open].split_whitespace();
        let Some(traditional) = words.next() else { continue };
        let Some(simplified) = words.next() else { continue };
        let key = normalize_pinyin(&line[open + 1..close]);
        if key.is_empty() || !key.bytes().all(|b| b.is_ascii_lowercase()) { continue; }
        entries.entry(key).or_default().push((simplified.into(), traditional.into()));
    }

    let mut writer = BufWriter::new(File::create(output)?);
    writeln!(writer, "# CC-CEDICT derived pinyin index; fields: pinyin\\tsimplified\\ttraditional")?;
    let mut count = 0usize;
    for (key, mut words) in entries {
        words.sort_by_key(|(simplified, _)| (priority_score(simplified, &priorities), simplified.clone()));
        let mut seen = HashSet::new();
        for (simplified, traditional) in words {
            if seen.insert((simplified.clone(), traditional.clone())) {
                writeln!(writer, "{key}\t{simplified}\t{traditional}")?;
                count += 1;
            }
        }
    }
    eprintln!("compiled {count} entries");
    Ok(())
}

fn load_unihan_grade_level(path: String) -> Result<HashMap<char, u8>, Box<dyn std::error::Error>> {
    let mut result = HashMap::new();
    for line in BufReader::new(File::open(path)?).lines() {
        let line = line?;
        let mut fields = line.split_whitespace();
        let Some(codepoint) = fields.next() else { continue };
        if fields.next() != Some("kGradeLevel") { continue; }
        let Some(value) = fields.next().and_then(|v| v.parse::<u8>().ok()) else { continue };
        let Some(hex) = codepoint.strip_prefix("U+") else { continue };
        if let Some(ch) = u32::from_str_radix(hex, 16).ok().and_then(char::from_u32) {
            result.insert(ch, value);
        }
    }
    Ok(result)
}

fn priority_score(word: &str, priorities: &HashMap<char, u8>) -> u16 {
    word.chars().map(|ch| u16::from(*priorities.get(&ch).unwrap_or(&9))).sum()
}

fn normalize_pinyin(value: &str) -> String {
    value.to_ascii_lowercase().replace("u:", "v").chars().filter_map(|ch| match ch {
        'a'..='z' => Some(ch.to_ascii_lowercase()),
        _ => None,
    }).collect()
}

#[cfg(test)]
mod tests {
    use super::normalize_pinyin;

    #[test]
    fn removes_tones_and_spaces() {
        assert_eq!(normalize_pinyin("Zhong1 guo2"), "zhongguo");
        assert_eq!(normalize_pinyin("lu:4"), "lv");
    }
}
