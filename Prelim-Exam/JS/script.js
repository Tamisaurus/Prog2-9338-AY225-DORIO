/* * Programmer: Raphael Louis P. Dorio [25-2280-916]
 * Project: Web Records - Dystopian Red/Black Theme
 */

let students = [];

async function init() {
    try {
        // 1. SHORTCUT: Read file directly (Requires Live Server)
        const response = await fetch('MOCK_DATA.csv');
        const data = await response.text();

        // 2. Parse Data
        const lines = data.trim().split('\n');
        
        // Skip header (i=1)
        for (let i = 1; i < lines.length; i++) {
            const cols = lines[i].split(',');
            if(cols.length > 1) {
                students.push({
                    id: cols[0],
                    first: cols[1],
                    last: cols[2],
                    grade: parseInt(cols[7]) // Convert to Integer for math sorting
                });
            }
        }
        render(); 

    } catch (error) {
        console.error("CSV Load Error:", error);
        alert("System Error: Use 'Live Server' to read local files.");
    }
}

function render() {
    const tbody = document.getElementById('tableBody');
    tbody.innerHTML = '';

    students.forEach((s, index) => {
        const row = `
            <tr>
                <td>${s.id}</td>
                <td>${s.first}</td>
                <td>${s.last}</td>
                <td><span style="color:${getGradeColor(s.grade)}">${s.grade}</span></td>
                <td><button class="btn-delete" onclick="deleteRecord(${index})">TERMINATE</button></td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

// --- NEW SORTING FEATURE ---
function sortData(type) {
    if (type === 'name') {
        // Sort A-Z by Last Name
        students.sort((a, b) => a.last.localeCompare(b.last));
    } else if (type === 'grade_desc') {
        // Highest to Lowest
        students.sort((a, b) => b.grade - a.grade);
    } else if (type === 'grade_asc') {
        // Lowest to Highest
        students.sort((a, b) => a.grade - b.grade);
    }
    render(); // Re-draw table after sorting
}

// --- CRUD LOGIC ---
function addRecord() {
    const id = document.getElementById('stuID').value;
    const first = document.getElementById('fName').value;
    const last = document.getElementById('lName').value;
    const grade = document.getElementById('grade').value;

    if(id && first) {
        students.push({ 
            id, 
            first, 
            last, 
            grade: parseInt(grade) || 0 
        });
        
        render();
        // Clear inputs
        document.querySelectorAll('input').forEach(i => i.value = '');
    } else {
        alert("Input Error: Missing ID or Name");
    }
}

function deleteRecord(index) {
    if(confirm("Confirm Termination of Record?")) {
        students.splice(index, 1);
        render();
    }
}

// Helper: Color code low grades for visual flair
function getGradeColor(grade) {
    if (grade >= 75) return '#f0f0f0'; // White for passing
    return '#b40000'; // Red for failing
}

init();