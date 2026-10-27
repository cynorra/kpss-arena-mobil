// docs/sorular_final.json uretici.
// Girdi : docs/sorular1.json (eski havuz), docs/question-pattern.json (ilk 4 ornek), docs/sorular2.json (yeni sorular)
// Cikti : docs/sorular_final.json  -> scripts/replaceQuestionPool.js ile uretim havuzunun yerine konur.
//
// Neden: eski havuzda dogru cevap %75 oraninda "A" idi, bazi sorular hatali/belirsizdi
// (ornegin Matematik #1'in isaretli cevabi yanlisti). Hatalilar cikarildi veya duzeltildi,
// kalan sorularin siklari dengeli dagitildi.
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const dir = __dirname;
const read = (f) => JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
const old = read('sorular1.json');
const pattern = read('question-pattern.json').slice(0, 4); // 5. (yasama - tekrar) ve 6. (Anthropic) alinmaz
const fresh = read('sorular2.json');
const LETTERS = ['A', 'B', 'C', 'D'];
const OPT = { A: 'optionA', B: 'optionB', C: 'optionC', D: 'optionD' };

// ---- hatali/belirsiz eski sorular (metnin basina gore) -> cikarilir
const DROP = [
  '"I. Ne var ki aradan', // paragraf sirasi: baglac sirasi celiskili
  'Aşağıdaki cümlelerin hangisinde büyük ünlü uyumuna', // birden fazla secenekte uyum bozuk (bahce, kalem)
  'Aşağıdaki cümlelerin hangisinde virgülün kaldırılması',
  'Aşağıdaki cümlelerin hangisinde "-den" eki farklı', // secenekler belirsiz
  'Aşağıdaki altı çizili zamirlerden', // altı çizili gosterilmiyor, "onu" belirsiz
  'Aşağıdaki cümlelerin hangisinde altı çizili sözcük özne', // altı çizili gosterilmiyor
  '"Kardeşim, yarınki sınava', // isaretli cevap yanlis ("sinava" dolayli tumlec)
  'Aşağıdaki cümlelerden hangisi yalnızca özne', // isaretli cevap yanlis (dogrusu D)
  'Bir davette Ali, Burcu', // tanimsiz oturma duzeni
  'Bir sıralamada Elif', // sonuc belirlenemiyor
  'Bir davette beş kişi', // tanimsiz
  'Bir kod sisteminde 5379', // tutarsiz kodlama
  "2023'te vefat eden", // dogrulanamayan bilgi
  "Türkiye'de çıkarılan enerji kaynaklarından biri olan bor", // bor enerji kaynagi degil, "en cok" belirsiz
  "Türkiye'de turizm gelirlerinin en yüksek", // il bazinda kesin veri belirsiz
];
// ---- duzeltilen eski sorular
const FIX = [
  { start: 'Rakamları birbirinden farklı üç basamaklı en büyük çift', correctText: '1089',
    explanation: 'Rakamları farklı en büyük çift sayı 986, rakamları farklı en küçük tek sayı 103 olduğundan toplam 986 + 103 = 1089.' },
];
// ---- "alti cizili" ifadesi gosterilmeyen sorular: metin netlestirilir
const REWORD = [
  ['Aşağıdaki cümlelerin hangisinde altı çizili sözcük mecaz anlamda kullanılmıştır?', 'Aşağıdaki cümlelerin hangisinde "ağır" sözcüğü mecaz anlamda kullanılmıştır?'],
  ['cümlesinde geçen altı çizili söz öbeğiyle', 'cümlesinde geçen "ayna tutmalıdır" söz öbeğiyle'],
];

// ---- onceki calistirmadan id'leri koru (ayni metin -> ayni id)
const outPath = path.join(dir, 'sorular_final.json');
const prevIds = {};
if (fs.existsSync(outPath)) for (const q of JSON.parse(fs.readFileSync(outPath, 'utf8'))) prevIds[q.text] = q.id;
function uuid() {
  const b = crypto.randomBytes(16);
  b[6] = (b[6] & 0x0f) | 0x40; b[8] = (b[8] & 0x3f) | 0x80;
  const h = b.toString('hex');
  return `${h.slice(0, 8)}-${h.slice(8, 12)}-${h.slice(12, 16)}-${h.slice(16, 20)}-${h.slice(20)}`;
}
let seed = 1982;
function rnd() { seed = (seed * 1664525 + 1013904223) % 4294967296; return seed / 4294967296; }

// ---- eski + ornek sorulari hazirla
const carried = [];
let dropped = 0;
for (const q0 of [...old, ...pattern]) {
  if (DROP.some((d) => q0.text.startsWith(d))) { dropped++; continue; }
  const q = { ...q0 };
  const fix = FIX.find((f) => q.text.startsWith(f.start));
  if (fix) {
    q[OPT[q.correct]] = q[OPT[q.correct]]; // noop
    const letter = LETTERS.find((l) => q[OPT[l]] === fix.correctText);
    if (!letter) throw new Error('duzeltme secenegi bulunamadi: ' + fix.correctText);
    q.correct = letter;
    q.explanation = fix.explanation;
  }
  for (const [from, to] of REWORD) q.text = q.text.replace(from, to);
  carried.push(q);
}

// ---- eski sorularin siklarini karistir; genel dagilim (yeni sorular dahil) dengeli kalsin
const counts = { A: 0, B: 0, C: 0, D: 0 };
for (const q of fresh) counts[q.correct]++;
const reshuffled = carried.map((q) => {
  const correctText = q[OPT[q.correct]];
  const texts = LETTERS.map((l) => q[OPT[l]]);
  const arrangements = [];
  for (let t = 0; t < 16; t++) {
    const a = texts.slice();
    for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(rnd() * (i + 1)); [a[i], a[j]] = [a[j], a[i]]; }
    arrangements.push(a);
  }
  arrangements.sort((x, y) => counts[LETTERS[x.indexOf(correctText)]] - counts[LETTERS[y.indexOf(correctText)]]);
  const best = arrangements[0];
  const letter = LETTERS[best.indexOf(correctText)];
  counts[letter]++;
  return {
    text: q.text, optionA: best[0], optionB: best[1], optionC: best[2], optionD: best[3],
    correct: letter, category: q.category, topic: q.topic || '', explanation: q.explanation || '',
  };
});

const final = [...reshuffled, ...fresh.map((q) => ({
  text: q.text, optionA: q.optionA, optionB: q.optionB, optionC: q.optionC, optionD: q.optionD,
  correct: q.correct, category: q.category, topic: q.topic, explanation: q.explanation,
}))].map((q) => ({ id: prevIds[q.text] || uuid(), ...q }));

// ---- dogrulama
const CATS = ['Türkçe', 'Matematik', 'Tarih', 'Coğrafya', 'Vatandaşlık', 'Güncel Bilgiler'];
const seen = new Set();
const problems = [];
for (const q of final) {
  for (const f of ['text', 'optionA', 'optionB', 'optionC', 'optionD', 'correct', 'category', 'explanation']) if (!q[f]) problems.push(`eksik ${f}: ${q.text.slice(0, 40)}`);
  if (!LETTERS.includes(q.correct)) problems.push(`gecersiz correct: ${q.text.slice(0, 40)}`);
  if (!CATS.includes(q.category)) problems.push(`gecersiz kategori: ${q.text.slice(0, 40)}`);
  if (new Set([q.optionA, q.optionB, q.optionC, q.optionD]).size !== 4) problems.push(`tekrarlayan sik: ${q.text.slice(0, 40)}`);
  if (seen.has(q.text)) problems.push(`tekrarlayan soru: ${q.text.slice(0, 40)}`);
  seen.add(q.text);
}
if (problems.length) { console.error(problems.join('\n')); process.exit(1); }

fs.writeFileSync(outPath, JSON.stringify(final, null, 2) + '\n', 'utf8');
const byCat = {};
final.forEach((q) => { byCat[q.category] = (byCat[q.category] || 0) + 1; });
const letters = { A: 0, B: 0, C: 0, D: 0 };
final.forEach((q) => letters[q.correct]++);
console.log(`Eski/ornek: ${old.length + pattern.length}, cikarilan: ${dropped}, yeni: ${fresh.length}`);
console.log('Toplam:', final.length, byCat);
console.log('Dogru sik dagilimi:', letters);
const need = { 'Türkçe': 30, 'Matematik': 30, 'Tarih': 27, 'Coğrafya': 18, 'Vatandaşlık': 9, 'Güncel Bilgiler': 6 };
for (const c of CATS) if ((byCat[c] || 0) < need[c]) console.log('UYARI: tam deneme icin yetersiz:', c);
