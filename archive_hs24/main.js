const progressBar = document.getElementById("progress-bar");
const startOfSemester = new Date(Date.UTC(2024, 8, 16)).getTime();
const endOfSemester = new Date(Date.UTC(2024, 11, 20)).getTime();
const now = Date.now();
const total = endOfSemester - startOfSemester;
const current = now - startOfSemester;
const percentage = Math.min(100, Math.ceil((current / total) * 100));
progressBar.style.width = percentage + "%";

/*
const regex = /((?:[\s\S]*\s+)?for\s*\(([^;]*)\s*;\s*([^;]*)\s*;\s*(.*)\s*\)\s*{[\s\S]*}[\s\S]*)/;

const groups = "for (int i = 0; i < 10; i++) {}".match(regex);
console.log("Gruppe 2: " + groups[2].trim() + " FIN");
console.log(groups[3].trim());
console.log(groups[4].trim());



*/