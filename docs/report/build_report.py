"""Build the Lab 1 assignment report (.docx) -> docs/report/GymBuddy_Lab1_Report.docx"""
import os
from docx import Document
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Pt, RGBColor

HERE = os.path.dirname(os.path.abspath(__file__))
LOGO = os.path.join(HERE, "wsb_logo.jpg")
MOCK = os.path.join(HERE, "..", "design", "mockups", "00_overview.png")
REPO = "https://github.com/Ozodiy/gymbuddy-app"

PINK = RGBColor(0xC8, 0x10, 0x5F)
GREY = RGBColor(0x55, 0x55, 0x55)

doc = Document()
sec = doc.sections[0]
sec.page_width, sec.page_height = Cm(21), Cm(29.7)
sec.left_margin = sec.right_margin = Cm(2.3)
sec.top_margin = sec.bottom_margin = Cm(2.2)

normal = doc.styles["Normal"]
normal.font.name = "Calibri"
normal.element.rPr.rFonts.set(qn("w:eastAsia"), "Calibri")
normal.font.size = Pt(11)
normal.paragraph_format.space_after = Pt(6)
for name, size in (("Heading 1", 16), ("Heading 2", 13)):
    st = doc.styles[name]
    st.font.name = "Calibri"
    st.element.rPr.rFonts.set(qn("w:eastAsia"), "Calibri")
    st.font.size = Pt(size)
    st.font.bold = True
    st.font.color.rgb = PINK
    st.paragraph_format.space_before = Pt(14)
    st.paragraph_format.space_after = Pt(6)


def para(text="", bold=False, size=None, color=None, align=None, italic=False, after=None):
    p = doc.add_paragraph()
    r = p.add_run(text)
    r.bold, r.italic = bold, italic
    if size:
        r.font.size = Pt(size)
    if color:
        r.font.color.rgb = color
    if align is not None:
        p.alignment = align
    if after is not None:
        p.paragraph_format.space_after = Pt(after)
    return p


def bullet(text, bold_lead=None):
    p = doc.add_paragraph(style="List Bullet")
    if bold_lead:
        p.add_run(bold_lead).bold = True
    p.add_run(text)
    p.paragraph_format.space_after = Pt(3)
    return p


def shade(cell, hex_fill):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:val"), "clear")
    shd.set(qn("w:color"), "auto")
    shd.set(qn("w:fill"), hex_fill)
    tcPr.append(shd)


def table(rows, widths, header=True, size=10):
    t = doc.add_table(rows=len(rows), cols=len(rows[0]))
    t.style = "Table Grid"
    t.alignment = WD_TABLE_ALIGNMENT.CENTER
    for i, row in enumerate(rows):
        for j, val in enumerate(row):
            c = t.cell(i, j)
            c.width = Cm(widths[j])
            c.text = ""
            p = c.paragraphs[0]
            p.paragraph_format.space_after = Pt(2)
            r = p.add_run(val)
            r.font.size = Pt(size)
            if header and i == 0:
                r.bold = True
                r.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
                shade(c, "C8105F")
            elif i % 2 == 0:
                shade(c, "F8E8EF")
    doc.add_paragraph().paragraph_format.space_after = Pt(2)
    return t


# ---------------- Cover page ----------------
p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
p.paragraph_format.space_before = Pt(20)
p.add_run().add_picture(LOGO, width=Cm(15.5))

para("", after=30)
para("Lab 1", True, 26, PINK, WD_ALIGN_PARAGRAPH.CENTER, after=4)
para("Assignment report", False, 14, GREY, WD_ALIGN_PARAGRAPH.CENTER, after=26)
para("Team Project – Mobile Application in the Cloud", True, 18, None, WD_ALIGN_PARAGRAPH.CENTER, after=4)
para("Project: GymBuddy", False, 14, None, WD_ALIGN_PARAGRAPH.CENTER, after=40)

cover = [
    ("Subject", "Team Project – Mobile Application in the Cloud"),
    ("Professor", "Mateusz Kawulok"),
    ("Team members", "Aida Ismayilova (57689)\nOzodbek Tursunaliyev (57264)\nElturan Valizada (58990)"),
    ("Repository", REPO),
    ("Date", "23 October 2026"),
]
t = doc.add_table(rows=len(cover), cols=2)
t.alignment = WD_TABLE_ALIGNMENT.CENTER
for i, (k, v) in enumerate(cover):
    a, b = t.cell(i, 0), t.cell(i, 1)
    a.width, b.width = Cm(4), Cm(11)
    a.text, b.text = "", ""
    ra = a.paragraphs[0].add_run(k)
    ra.bold = True
    ra.font.color.rgb = PINK
    b.paragraphs[0].add_run(v)
    for c in (a, b):
        c.paragraphs[0].paragraph_format.space_after = Pt(6)
doc.add_page_break()

# ---------------- 1 Summary ----------------
doc.add_heading("1. Summary", 1)
para("For our team project we are building GymBuddy, an Android app written in Kotlin with Jetpack Compose and "
     "a Firebase backend. In Lab 1 we set up the basics: the idea, the GitHub repository, the Android project and "
     "a first working feature, login with Firebase Authentication (register, log in, log out and staying logged in).")
para("Repository: " + REPO)

# ---------------- 2 Team ----------------
doc.add_heading("2. Team and roles", 1)
para("There are three of us and four roles in the lab, so we share them. Everybody also writes code.")
table([
    ["Member", "Student ID", "Roles", "Main responsibilities"],
    ["Aida Ismayilova", "57689", "Product Owner + Developer",
     "Problem statement, MVP, user stories and priorities"],
    ["Ozodbek Tursunaliyev", "57264", "Tech Lead + Developer",
     "Data model, architecture, Android and Firebase setup, authentication feature"],
    ["Elturan Valizada", "58990", "QA / DevOps + Developer",
     "Repository settings, branch ruleset, pull request reviews, board hygiene, testing"],
], [3.6, 2.0, 3.8, 6.9])

# ---------------- 3 Product ----------------
doc.add_heading("3. Product definition", 1)
doc.add_heading("3.1 App name and problem statement", 2)
para("The app is called GymBuddy. In the gym a “buddy” is your training partner, so the name fits an app that is both a workout diary and a way to find someone to train with.")
para("“Gym-goers forget what they trained and struggle to find training partners, so progress stalls and "
     "motivation drops.”", italic=True)

doc.add_heading("3.2 What the project means and where it can be used", 2)
para("Gym + Buddy. People keep training when they can see what they have already done and when they have someone "
     "to train with, and GymBuddy gives them both. It is an Android app with a cloud backend. Workouts and training "
     "sessions are saved in Cloud Firestore, so the data is still there if the user changes phones, and Firebase "
     "Authentication makes sure each person only sees their own data.")
table([
    ["Where it can be used", "How GymBuddy helps"],
    ["Individual gym-goers", "Keep a simple training history and see weekly progress"],
    ["Friends and training groups", "Plan shared sessions and see who joined"],
    ["Newcomers to a gym", "Find a partner instead of training alone"],
    ["University sports clubs", "Organise open sessions for students and staff"],
    ["Workplace wellness programmes", "Let colleagues arrange group workouts"],
    ["Small gyms and fitness studios", "Give members a free way to meet training partners"],
], [5.6, 10.7])
para("Why it is useful: people who write down their training can see their progress, people who train with a "
     "partner skip fewer sessions, and nothing gets lost like paper notes. Pull request #7 added this overview to "
     "the repository (docs/planning/project-overview.md).")

doc.add_heading("3.3 MVP features", 2)
table([
    ["#", "Feature", "Sprint"],
    ["1", "Register, log in, log out, auto-login (Firebase Authentication)", "Sprint 1 (Lab 1)"],
    ["2", "List my workouts", "Sprint 1 (Lab 2)"],
    ["3", "Add, edit and delete a workout (detail/edit screen)", "Sprint 1 (Lab 2)"],
    ["4", "Weekly stats calculated by a Cloud Function", "Later (Lab 3)"],
    ["5", "Find a partner: create and join training sessions", "Later"],
], [1.0, 11.0, 4.3])
para("For Lab 3 we will add a Cloud Function that calculates the weekly stats. The app has 5 screens (Login, "
     "Register, Workouts, Workout detail/edit, Partners) and 3 Firestore collections, which stays within the limits "
     "of the course.")

doc.add_heading("3.4 Planned screens", 2)
doc.add_picture(MOCK, width=Cm(16))
doc.paragraphs[-1].alignment = WD_ALIGN_PARAGRAPH.CENTER
para("Figure 1. Concept mockups of the five screens (Login, Register, Workouts, Workout detail/edit, Partners).",
     italic=True, size=9, color=GREY, align=WD_ALIGN_PARAGRAPH.CENTER)
para("A short description of each screen in Figure 1:")
table([
    ["Screen", "What the picture shows", "Used for"],
    ["1. Login", "App logo, email and password fields, orange Log in button, link “No account? Register”",
     "Returning users sign in; a logged-in user skips this screen (auto-login)"],
    ["2. Register", "Email, password and confirm-password fields with red error messages (invalid email, "
                    "at least 6 characters, passwords do not match)",
     "Creating an account; errors appear before Firebase is called"],
    ["3. Workouts", "Weekly summary (3 workouts, 165 min), list of workout cards (title, type, duration, date), "
                    "round + button, bottom tabs Workouts and Partners, Log out",
     "Home screen: review training history and start a new workout"],
    ["4. Workout detail/edit", "Fields title, type, duration, date and notes, Save button, red Delete workout button",
     "Adding, editing or deleting one workout"],
    ["5. Partners", "Cards for open training sessions (title, gym and time, 2/4 joined) with Join / Joined buttons",
     "Creating and joining sessions to find a training partner"],
], [3.2, 7.6, 5.5], size=9)

doc.add_heading("3.5 Data model (Cloud Firestore)", 2)
para("Every document has an ownerId field that says who it belongs to. In Lab 2 we will write Security Rules so users can only change their own data.")
table([
    ["Collection", "Fields (type)"],
    ["users", "uid (doc id), email (string), displayName (string), createdAt (timestamp)"],
    ["workouts", "ownerId (string), title (string), type (string), durationMin (number), notes (string), "
                 "date (timestamp), createdAt (timestamp)"],
    ["sessions", "ownerId (string), title (string), gym (string), startsAt (timestamp), maxPeople (number), "
                 "participantIds (array of string)"],
], [3.0, 13.3])

# ---------------- 4 Architecture ----------------
doc.add_heading("4. Architecture", 1)
para("The app talks to Firebase Authentication and Cloud Firestore through the Firebase SDKs. Cloud Functions "
     "come in Lab 3. Inside the app the code is split into three layers, so the screens never call Firebase directly.")
table([
    ["Layer", "Classes", "Responsibility"],
    ["Data", "AuthRepository, FirebaseAuthRepository", "The only code that talks to FirebaseAuth"],
    ["Logic", "AuthValidator, AuthViewModel", "Input validation, UI state (StateFlow), calls the repository"],
    ["UI", "LoginScreen, RegisterScreen, HomeScreen, AppNavHost", "Show state and forward user actions"],
], [2.2, 6.6, 7.5])
para("Navigation follows AuthViewModel.isLoggedIn, which comes from the Firebase auth-state listener. "
     "AuthValidator does not use any Android classes, so we can unit-test it on the JVM in Lab 3. "
     "The README also has a Mermaid architecture diagram.")

# ---------------- 5 Repository & board ----------------
doc.add_heading("5. GitHub repository and sprint board", 1)
bullet(" " + REPO + " (public; we created it empty and then pushed the project).", "Repository:")
bullet(" an initial Android project commit, the authentication feature commit, and a documentation commit "
       "(README, planning documents, mockups, sprint issues, presentation, pull request template).", "Commits on main:")
bullet(" .github/pull_request_template.md and README.md with the required sections.", "Templates:")
bullet(" the labels user-story, android, firebase, ui, bug and docs exist. The sprint board with the columns "
       "Backlog, Sprint 1, In progress, In review and Done is the next step.", "Board:")
bullet(" the ruleset protect-main (pull request required, no force push, no deletion) is still to be set up.",
       "Branch protection:")

doc.add_heading("5.1 User stories (Sprint 1)", 2)
para("We wrote the user stories in the format from the lab instructions: a title, a short story and a checklist "
     "of acceptance criteria. Each story is a GitHub issue with labels. Example:")
para("Title: As a gym-goer, I want to register and log in with email and password", bold=True)
para("User story: As a gym-goer, I want to register and log in with my email and password, so that I can access my own workouts.")
for c in ("Valid credentials open the home screen", "Wrong credentials show an error message",
          "A logged-in user skips the login screen after restarting the app",
          "Logout returns to login and Back does not return to home"):
    bullet(" " + c, "☐")
table([
    ["Sprint 1 user story", "Labels", "Assignee"],
    ["As a gym-goer, I want to register and log in with email and password", "user-story, android, firebase", "Ozodbek"],
    ["As a gym-goer, I want to see a list of my workouts", "user-story, android, ui", "Elturan"],
    ["As a gym-goer, I want to add a workout", "user-story, android, firebase", "Aida"],
    ["As a gym-goer, I want to edit or delete a workout", "user-story, android, firebase", "Elturan"],
], [8.6, 5.0, 2.7])
para("Issues #1 to #4 are the Sprint 1 stories above. #5 (weekly stats) and #6 (find and join training sessions) "
     "are in the backlog. All six are in the repository with labels. Only issue #1 has an assignee on GitHub so far; "
     "the others will be assigned when the teammates are added to the repository.")

# ---------------- 6 Android & Firebase ----------------
doc.add_heading("6. Android project and Firebase", 1)
bullet(" Android Studio, Kotlin, Jetpack Compose, minimum SDK 26, Gradle Kotlin DSL with a version catalog.", "Project:")
bullet(" com.ozodbek.gymbuddy.", "Package name:")
bullet(" Firebase BoM 34.17.0 (firebase-auth), Navigation Compose 2.9.0, lifecycle-viewmodel-compose 2.9.0, "
       "kotlinx-coroutines-play-services 1.10.2, Google Services plugin 4.4.3.", "Dependencies:")
bullet(" the next steps are to create the Firebase project (free Spark plan), register the Android app with the "
       "package name above, put google-services.json in app/, enable Email/Password sign-in, and add the teammates "
       "and the instructor as members.", "Firebase:")

# ---------------- 7 Feature ----------------
doc.add_heading("7. Feature: Firebase Authentication", 1)
para("The feature is written with the three layers from section 4. It still has to be tested on the emulator once Firebase is connected.")
bullet(" AuthRepository (interface) and FirebaseAuthRepository wrap register, login, logout and an auth-state Flow; "
       "calls use runCatching and return Result<Unit>.", "Data:")
bullet(" AuthValidator has pure functions validateEmail, validatePassword (minimum 6 characters) and "
       "validatePasswordConfirmation. AuthViewModel keeps AuthUiState (fields, field errors, general error, isLoading) "
       "and exposes isLoggedIn as a StateFlow.", "Logic:")
bullet(" LoginScreen, RegisterScreen and a temporary HomeScreen (app name, problem statement, “Logged in as <email>”, "
       "Log out).", "UI:")
bullet(" the start destination is home when a user is already logged in, otherwise login (auto-login). "
       "On login, register and logout the back stack is cleared with popUpTo(graph.id) { inclusive = true } and launchSingleTop.",
       "Navigation:")
bullet(" Firebase exceptions are mapped to friendly messages, for example FirebaseAuthUserCollisionException becomes "
       "“An account with this email already exists”. No raw exception text is shown.", "Errors:")
bullet(" AuthValidatorTest (JUnit) covers valid and invalid email, short password and password mismatch.", "Tests:")

doc.add_heading("7.1 Self-test checklist", 2)
table([
    ["Test", "Result"],
    ["A new account can be created; the user lands on Home", "☐"],
    ["Invalid email, short password and mismatched passwords show field errors without calling Firebase", "☐"],
    ["An existing email on registration shows a readable message", "☐"],
    ["Wrong password shows a readable message", "☐"],
    ["Logout returns to Login; Back does not return to Home", "☐"],
    ["After closing and reopening the app a logged-in user goes straight to Home", "☐"],
    ["The new user is visible in Firebase Console → Authentication → Users", "☐"],
], [14.0, 2.3])

# ---------------- 8 PR ----------------
doc.add_heading("8. Pull requests and review", 1)
para("After the first commits, every change goes through a feature branch and a pull request into main. Another "
     "team member should leave a review comment and approve it, and then we use Squash and merge. Each PR uses the "
     "template and, when it belongs to an issue, says “Closes #<issue>”.")
table([
    ["PR", "Title", "Author", "Reviewer", "Linked issue"],
    ["#7", "docs: explain project meaning and use cases", "Ozodiy (Ozodbek)", "No review yet", "None (docs only)"],
], [1.5, 6.5, 2.8, 2.8, 2.7])
para("PR #7 (https://github.com/Ozodiy/gymbuddy-app/pull/7) came from the branch docs/project-overview, uses the "
     "PR template and is merged into main. It adds docs/planning/project-overview.md and links it from the README. "
     "It has no teammate review yet, so we do not meet the requirement of two reviewed PRs yet. The next PRs will "
     "get a review comment and an approval from another member.")

# ---------------- 9 Retro ----------------
doc.add_heading("9. Demo and retrospective", 1)
para("Demo plan (3 minutes): Aida explains the problem and the MVP. Ozodbek shows the data model and the "
     "architecture and then does the live demo: register, log out, log in, and restart the app to show auto-login. "
     "Elturan shows the board, the merged PRs with review comments and the branch ruleset.")
para("After the demo we will write the Retro – Lab 1 issue on GitHub (label docs). Everyone adds a comment: what "
     "went well, what did not, and what we will change. The action items go here:")
table([
    ["Went well", "Did not go well", "We will change"],
    ["", "", ""],
], [5.4, 5.4, 5.5])

# ---------------- 10 Checklist ----------------
doc.add_heading("10. End-of-lab checklist", 1)
table([
    ["Item", "Status"],
    ["Roles, problem statement, MVP and data model in README", "☑"],
    ["Architecture diagram (Mermaid) in README", "☑"],
    ["Board with 5 columns; Sprint 1 issues with acceptance criteria and assignees", "☐"],
    ["Empty repo and initial push to main", "☑"],
    ["Ruleset protecting main (PR required, no force push)", "☐"],
    ["PR template in .github/", "☑"],
    ["At least two merged PRs, each with a review", "☐"],
    ["Firebase project (Spark plan), team and instructor added", "☐"],
    ["App registered, google-services.json in app/", "☐"],
    ["Email/Password sign-in enabled", "☐"],
    ["Register, login, logout and auto-login work", "☐"],
    ["Readable error messages, no crash on wrong input", "☐"],
    ["Retro – Lab 1 issue with action items", "☐"],
], [14.0, 2.3])

# ---------------- 11 Tools ----------------
doc.add_heading("11. Tools used", 1)
table([
    ["Phase", "Tools"],
    ["Planning and setup", "Markdown, Mermaid, GitHub Issues and Projects, Git, GitHub, GitHub CLI"],
    ["Design", "Python with Pillow (mockups)"],
    ["Development", "Android Studio, Kotlin, Jetpack Compose, Navigation Compose, ViewModel and StateFlow, Coroutines, Gradle"],
    ["Cloud backend", "Firebase Authentication, Cloud Firestore, Security Rules, Cloud Functions (Lab 3)"],
    ["Testing", "JUnit, Android emulator"],
    ["Documentation", "python-docx, python-pptx (report and presentation)"],
], [4.0, 12.3])

# ---------------- 12 Screenshots ----------------
doc.add_page_break()
doc.add_heading("12. Screenshots", 1)
para("These screenshots come from the public GitHub repository, so they show the pages without being logged in.")
SHOT_DIR = os.path.join(HERE, "screenshots")
SHOTS = [
    ("01_repo.png", "GitHub repository gymbuddy-app: the folders and files on main, the three initial commits, "
                    "the description with the problem statement, and the README with the app name."),
    ("02_commits.png", "Commit history of main: the initial Android project, the authentication feature and the documentation."),
    ("03_issues.png", "The six issues: four Sprint 1 user stories and two backlog stories, with labels."),
    ("04_issue1.png", "Issue #1 in the lab format: user story, acceptance criteria checklist, labels android, firebase and "
                      "user-story, and an assignee."),
    ("05_pr7.png", "Pull request #7 (docs: explain project meaning and use cases): the filled-in PR template, "
                   "the docs label and the merged status."),
]
for n, (fname, cap) in enumerate(SHOTS, 1):
    path = os.path.join(SHOT_DIR, fname)
    if not os.path.exists(path):
        continue
    pp = doc.add_paragraph()
    pp.alignment = WD_ALIGN_PARAGRAPH.CENTER
    pp.paragraph_format.keep_with_next = True
    pp.paragraph_format.space_after = Pt(2)
    pp.add_run().add_picture(path, width=Cm(14))
    para(f"Screenshot {n}. {cap}", italic=True, size=9, color=GREY, align=WD_ALIGN_PARAGRAPH.CENTER, after=12)

out = os.environ.get("REPORT_OUT") or os.path.join(HERE, "GymBuddy_Lab1_Report_v2.docx")
doc.save(out)
print("saved", out)

