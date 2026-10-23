"""Generate GymBuddy phone-screen mockups (PNG) with Pillow -> docs/mockups/."""
import os
from PIL import Image, ImageDraw, ImageFont

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, "mockups")
os.makedirs(OUT, exist_ok=True)

W, H = 720, 1480
BG, CARD, INK, MUTED = "#F8FAFC", "#FFFFFF", "#0F172A", "#64748B"
ACCENT, ACCENT_DK, LINE, ERR = "#F97316", "#C2410C", "#E2E8F0", "#DC2626"
NAVY = "#111827"


def font(size, bold=False):
    name = "arialbd.ttf" if bold else "arial.ttf"
    return ImageFont.truetype(os.path.join(os.environ.get("WINDIR", "C:\\Windows"), "Fonts", name), size)


def new_screen():
    img = Image.new("RGB", (W, H), BG)
    d = ImageDraw.Draw(img)
    d.text((40, 24), "9:41", font=font(26, True), fill=INK)
    d.text((W - 150, 24), "5G  100%", font=font(24), fill=INK)
    return img, d


def text(d, xy, s, size=30, bold=False, fill=INK, anchor="la"):
    d.text(xy, s, font=font(size, bold), fill=fill, anchor=anchor)


def field(d, y, label, value="", error=None, password=False):
    text(d, (60, y), label, 24, fill=MUTED)
    d.rounded_rectangle((60, y + 36, W - 60, y + 120), 16, fill=CARD, outline=ERR if error else LINE, width=3)
    shown = "\u2022" * len(value) if password else value
    text(d, (84, y + 62), shown, 30, fill=INK)
    if error:
        text(d, (64, y + 128), error, 22, fill=ERR)
    return y + (170 if error else 150)


def button(d, y, label, color=ACCENT, fg="white"):
    d.rounded_rectangle((60, y, W - 60, y + 96), 48, fill=color)
    text(d, (W // 2, y + 48), label, 32, True, fg, "mm")
    return y + 120


def top_bar(d, title, back=False, action=None):
    d.rectangle((0, 70, W, 190), fill=NAVY)
    text(d, (60 if not back else 110, 130), title, 38, True, "white", "lm")
    if back:
        text(d, (50, 130), "\u2039", 56, True, "white", "lm")
    if action:
        text(d, (W - 50, 130), action, 28, True, ACCENT, "rm")


def bottom_nav(d, active):
    d.rectangle((0, H - 130, W, H), fill=CARD)
    d.line((0, H - 130, W, H - 130), fill=LINE, width=2)
    for i, (label, key) in enumerate([("Workouts", "w"), ("Partners", "p")]):
        cx = W // 4 + i * W // 2
        col = ACCENT_DK if key == active else MUTED
        d.ellipse((cx - 22, H - 112, cx + 22, H - 68), fill=col)
        text(d, (cx, H - 40), label, 26, key == active, col, "mm")


def logo(d, y):
    d.rounded_rectangle((W // 2 - 70, y, W // 2 + 70, y + 140), 36, fill=ACCENT)
    text(d, (W // 2, y + 70), "GB", 60, True, "white", "mm")
    text(d, (W // 2, y + 200), "GymBuddy", 54, True, INK, "mm")


def save(img, name):
    img.save(os.path.join(OUT, name))


# 1 Login ---------------------------------------------------------------
img, d = new_screen()
logo(d, 260)
text(d, (W // 2, 520), "Log in to continue", 30, fill=MUTED, anchor="mm")
y = field(d, 580, "Email", "alex@gmail.com")
y = field(d, y, "Password", "secret1", password=True)
button(d, y + 30, "Log in")
text(d, (W // 2, y + 190), "No account? Register", 28, True, ACCENT_DK, "mm")
save(img, "01_login.png")

# 2 Register (with field errors) ---------------------------------------
img, d = new_screen()
text(d, (60, 250), "Create account", 52, True)
y = field(d, 360, "Email", "alex@gmail", error="Enter a valid email address")
y = field(d, y, "Password", "123", password=True, error="At least 6 characters")
y = field(d, y, "Confirm password", "1234", password=True, error="Passwords do not match")
button(d, y + 20, "Create account")
text(d, (W // 2, y + 180), "Already have an account? Log in", 28, True, ACCENT_DK, "mm")
save(img, "02_register.png")

# 3 Home: workout list --------------------------------------------------
img, d = new_screen()
top_bar(d, "My workouts", action="Log out")
text(d, (60, 220), "This week: 3 workouts \u00b7 165 min", 28, True, ACCENT_DK)
items = [("Push day", "Strength \u00b7 60 min", "Mon 20 Oct"),
         ("5 km run", "Cardio \u00b7 35 min", "Wed 22 Oct"),
         ("Leg day", "Strength \u00b7 70 min", "Fri 24 Oct"),
         ("Yoga flow", "Mobility \u00b7 40 min", "Sun 26 Oct")]
y = 290
for t, sub, date in items:
    d.rounded_rectangle((50, y, W - 50, y + 170), 24, fill=CARD, outline=LINE, width=2)
    d.rounded_rectangle((50, y, 66, y + 170), 8, fill=ACCENT)
    text(d, (96, y + 28), t, 34, True)
    text(d, (96, y + 82), sub, 26, fill=MUTED)
    text(d, (96, y + 122), date, 24, fill=MUTED)
    text(d, (W - 80, y + 85), "\u203a", 52, True, MUTED, "rm")
    y += 195
d.ellipse((W - 170, H - 330, W - 60, H - 220), fill=ACCENT)
text(d, (W - 115, H - 277), "+", 70, True, "white", "mm")
bottom_nav(d, "w")
save(img, "03_workouts.png")

# 4 Workout detail / edit ----------------------------------------------
img, d = new_screen()
top_bar(d, "Edit workout", back=True, action="Save")
y = field(d, 230, "Title", "Push day")
y = field(d, y, "Type", "Strength")
y = field(d, y, "Duration (min)", "60")
y = field(d, y, "Date", "20 Oct 2025")
text(d, (60, y), "Notes", 24, fill=MUTED)
d.rounded_rectangle((60, y + 36, W - 60, y + 246), 16, fill=CARD, outline=LINE, width=3)
text(d, (84, y + 62), "Bench 4x8 @ 70 kg, felt strong.", 28)
button(d, y + 300, "Delete workout", "#FEE2E2", ERR)
save(img, "04_workout_edit.png")

# 5 Partners: training sessions ----------------------------------------
img, d = new_screen()
top_bar(d, "Find a partner")
text(d, (60, 220), "Open sessions near you", 28, True, ACCENT_DK)
sessions = [("Chest & triceps", "FitZone Gym \u00b7 Sat 10:00", "2/4 joined", "Join"),
            ("Morning run", "City Park \u00b7 Sun 08:00", "3/6 joined", "Joined"),
            ("Leg day", "PowerHouse \u00b7 Mon 18:30", "1/3 joined", "Join")]
y = 290
for t, sub, cnt, btn in sessions:
    d.rounded_rectangle((50, y, W - 50, y + 230), 24, fill=CARD, outline=LINE, width=2)
    text(d, (84, y + 28), t, 34, True)
    text(d, (84, y + 84), sub, 26, fill=MUTED)
    text(d, (84, y + 130), cnt, 26, True, ACCENT_DK)
    joined = btn == "Joined"
    d.rounded_rectangle((W - 260, y + 140, W - 84, y + 204), 32, fill="#E2E8F0" if joined else ACCENT)
    text(d, (W - 172, y + 172), btn, 26, True, MUTED if joined else "white", "mm")
    y += 255
d.ellipse((W - 170, H - 330, W - 60, H - 220), fill=ACCENT)
text(d, (W - 115, H - 277), "+", 70, True, "white", "mm")
bottom_nav(d, "p")
save(img, "05_partners.png")

# Overview strip --------------------------------------------------------
names = ["01_login", "02_register", "03_workouts", "04_workout_edit", "05_partners"]
thumbs = [Image.open(os.path.join(OUT, n + ".png")).resize((W // 2, H // 2)) for n in names]
strip = Image.new("RGB", (len(thumbs) * (W // 2 + 30) + 30, H // 2 + 60), "#E2E8F0")
for i, t in enumerate(thumbs):
    strip.paste(t, (30 + i * (W // 2 + 30), 30))
strip.save(os.path.join(OUT, "00_overview.png"))
print("done", OUT)
