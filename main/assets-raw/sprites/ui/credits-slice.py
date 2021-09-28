from PIL import Image

a = Image.open("credits-icons.png")
b = [
	"ThePythonGuy",
	"GlennFolker",
	"EyeOfDarkness",
	"JerichoFletcher",
	"Goober",
	"sk7725",
	"MEEPofFaith",
	"ThirstyBoi",
	"Drullkus",
	"Xusk",
	"Evl",
	"BasedUser",
	"Anuke",
	"Xelo",
	"Eldoofus",
	"younggam",
	"Sharlotte",
	"BlueWolf"
]

x = 0
y = 0
for i in b:
	im = a.crop((x, y, x + 16, y + 16))
	x += 16
	im.save(i + ".png")