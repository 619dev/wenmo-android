//! Offline, deterministic input engine shared by every Wenmo platform shell.

#[derive(Clone, Copy, Debug, Default, Eq, PartialEq)]
pub enum Script {
    #[default]
    Simplified,
    Traditional,
}

#[derive(Debug, Default)]
pub struct Engine {
    composition: String,
    script: Script,
}

impl Engine {
    pub fn new() -> Self { Self::default() }

    pub fn type_ascii(&mut self, ch: char) {
        if ch.is_ascii_lowercase() { self.composition.push(ch); }
    }

    pub fn backspace(&mut self) { self.composition.pop(); }
    pub fn clear(&mut self) { self.composition.clear(); }
    pub fn composition(&self) -> &str { &self.composition }
    pub fn set_script(&mut self, script: Script) { self.script = script; }

    pub fn candidates(&self) -> &'static [&'static str] {
        match (self.script, self.composition.as_str()) {
            (Script::Simplified, "ni") => &["你", "呢", "泥", "拟"],
            (Script::Traditional, "ni") => &["你", "呢", "泥", "擬"],
            (Script::Simplified, "hao") => &["好", "号", "浩", "豪"],
            (Script::Traditional, "hao") => &["好", "號", "浩", "豪"],
            (Script::Simplified, "wenmo") => &["问墨", "文墨"],
            (Script::Traditional, "wenmo") => &["問墨", "文墨"],
            (Script::Simplified, "zhongguo") => &["中国"],
            (Script::Traditional, "zhongguo") => &["中國"],
            _ => &[],
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn composition_and_script_are_deterministic() {
        let mut engine = Engine::new();
        for ch in "wenmo".chars() { engine.type_ascii(ch); }
        assert_eq!(engine.composition(), "wenmo");
        assert_eq!(engine.candidates()[0], "问墨");
        engine.set_script(Script::Traditional);
        assert_eq!(engine.candidates()[0], "問墨");
    }

    #[test]
    fn ignores_non_lowercase_input() {
        let mut engine = Engine::new();
        for ch in "Ni你1".chars() { engine.type_ascii(ch); }
        assert_eq!(engine.composition(), "i");
    }
}

