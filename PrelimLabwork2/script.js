// --- INITIALIZATION ---
if (!localStorage.getItem('userDatabase')) {
    const defaultDB = [{ fullname: "Administrator", dept: "IT Admin", username: "admin", password: "1234" }];
    localStorage.setItem('userDatabase', JSON.stringify(defaultDB));
}

let currentUser = null; // Stores the entire user object

// --- CLOCK TICKER ---
setInterval(() => {
    const now = new Date();
    document.getElementById('live-clock').innerText = now.toLocaleTimeString('en-US', { hour12: false });
    document.getElementById('live-date').innerText = now.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
}, 1000);

// --- NAVIGATION ---
function showRegister() {
    toggleScreens('register-screen');
    clearInputs();
}
function showLogin() {
    toggleScreens('login-screen');
}
function logout() {
    currentUser = null;
    toggleScreens('login-screen');
}
function toggleScreens(id) {
    document.querySelectorAll('.screen, #dashboard-screen').forEach(el => el.classList.add('hidden'));
    document.getElementById(id).classList.remove('hidden');
}
function clearInputs() {
    document.querySelectorAll('input').forEach(input => input.value = '');
}

// --- LOGIC: REGISTER ---
function handleRegister() {
    const fullname = document.getElementById('reg-fullname').value.trim();
    const dept = document.getElementById('reg-dept').value.trim();
    const user = document.getElementById('reg-user').value.trim();
    const pass = document.getElementById('reg-pass').value.trim();

    if (!fullname || !dept || !user || !pass) { alert("Fill all fields"); return; }

    let db = JSON.parse(localStorage.getItem('userDatabase'));
    if (db.some(u => u.username.toLowerCase() === user.toLowerCase())) { 
        alert("Username taken."); return; 
    }

    db.push({ fullname, dept, username: user, password: pass });
    localStorage.setItem('userDatabase', JSON.stringify(db));

    alert("Account Created! Login now.");
    showLogin();
}

// --- LOGIC: LOGIN ---
function handleLogin() {
    const user = document.getElementById('login-user').value.trim();
    const pass = document.getElementById('login-pass').value.trim();
    
    let db = JSON.parse(localStorage.getItem('userDatabase'));
    const account = db.find(u => u.username === user && u.password === pass);

    if (account) {
        currentUser = account;
        document.getElementById('display-fullname').innerText = currentUser.fullname;
        document.getElementById('display-dept').innerText = currentUser.dept;
        
        toggleScreens('dashboard-screen');
        refreshTableDisplay();
        updateStatusMessage("Welcome back. Ready to log.");
    } else {
        alert("Invalid Credentials");
    }
}

// --- LOGIC: CLOCK IN / OUT ---
function userClockIn() {
    recordAction("TIME-IN");
}

function userClockOut() {
    recordAction("TIME-OUT");
}

function recordAction(type) {
    if (!currentUser) return;

    // 1. Check recent status
    const history = JSON.parse(localStorage.getItem('attendanceLogs')) || [];
    // Filter logs ONLY for this specific user
    const myLogs = history.filter(rec => rec.username === currentUser.username);
    const lastLog = myLogs[myLogs.length - 1];

    // 2. Validation
    if (type === "TIME-IN" && lastLog && lastLog.type === "TIME-IN") {
        updateStatusMessage("ERROR: You are already Clocked In!", "error");
        return;
    }
    if (type === "TIME-OUT" && (!lastLog || lastLog.type === "TIME-OUT")) {
        updateStatusMessage("ERROR: You are not Clocked In yet!", "error");
        return;
    }

    // 3. Create Record
    const now = new Date();
    const timestamp = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}:${String(now.getSeconds()).padStart(2,'0')}`;
    const idSig = Math.random().toString(36).substring(2, 10).toUpperCase();

    const newRecord = {
        username: currentUser.username, // Key link
        name: currentUser.fullname,
        dept: currentUser.dept,
        type: type,
        time: timestamp,
        id: idSig
    };

    history.push(newRecord);
    localStorage.setItem('attendanceLogs', JSON.stringify(history));

    // 4. Update UI
    updateStatusMessage(`SUCCESS: ${type} Recorded at ${timestamp}. E-Sign: ${idSig}`);
    refreshTableDisplay();
}

function updateStatusMessage(msg, type) {
    const el = document.getElementById('status-message');
    el.innerText = msg;
    el.style.color = type === "error" ? "#ff6464" : "#4CAF50";
}

function refreshTableDisplay() {
    if (!currentUser) return;
    
    const tbodyIn = document.querySelector('#tableIn tbody');
    const tbodyOut = document.querySelector('#tableOut tbody');
    tbodyIn.innerHTML = "";
    tbodyOut.innerHTML = "";

    const history = JSON.parse(localStorage.getItem('attendanceLogs')) || [];
    // Only show MY logs
    const myLogs = history.filter(rec => rec.username === currentUser.username);

    myLogs.forEach(rec => {
        const row = `<tr><td>${rec.time}</td><td style="font-family:monospace; color:#aaa;">${rec.id}</td></tr>`;
        if (rec.type === "TIME-IN") tbodyIn.innerHTML += row;
        else tbodyOut.innerHTML += row;
    });
}

function clearAttendanceLogs() {
    if (confirm("Clear YOUR attendance history?")) {
        let history = JSON.parse(localStorage.getItem('attendanceLogs')) || [];
        // Keep logs that are NOT mine
        const othersLogs = history.filter(rec => rec.username !== currentUser.username);
        localStorage.setItem('attendanceLogs', JSON.stringify(othersLogs));
        refreshTableDisplay();
        updateStatusMessage("History Cleared.");
    }
}

function downloadAttendanceLogs() {
    if (!currentUser) return;
    const history = JSON.parse(localStorage.getItem('attendanceLogs')) || [];
    const myLogs = history.filter(rec => rec.username === currentUser.username);
    
    let content = `ATTENDANCE LOG: ${currentUser.fullname}\nDEPARTMENT: ${currentUser.dept}\n==========================================\n\n`;
    content += "TYPE      | TIME                 | E-SIGNATURE\n";
    content += "--------------------------------------------------\n";

    myLogs.forEach(rec => {
        content += `${rec.type.padEnd(9)} | ${rec.time}  | ${rec.id}\n`;
    });

    const blob = new Blob([content], { type: 'text/plain' });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = `${currentUser.username}_Logs.txt`;
    link.click();
}

function downloadUserDatabase() {
    let db = JSON.parse(localStorage.getItem('userDatabase'));
    let content = "USER DB EXPORT\n==============\n";
    db.forEach(u => content += `User: ${u.username} | Pass: ${u.password} | Name: ${u.fullname} | Dept: ${u.dept}\n`);
    const blob = new Blob([content], { type: 'text/plain' });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = "database.txt";
    link.click();
}