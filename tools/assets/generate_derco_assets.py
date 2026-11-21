from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import json, math

OUT = Path(__file__).resolve().parent / 'png'
S = 4
BLUE, CYAN, PALE = (36,118,255), (42,226,255), (202,244,255)
NAVY, MID = (7,18,39), (12,32,62)
for sub in ('clickgui','categories','hud','modules','panels'):
    (OUT/'textures'/sub).mkdir(parents=True, exist_ok=True)

def q(v): return int(round(v*S))
def rgba(c,a=255): return (*c,a)
def canvas(w,h=None,bg=(0,0,0,0)):
    h = w if h is None else h
    return Image.new('RGBA',(w*S,h*S),bg)
def ln(d,pts,c=CYAN,w=3):
    p=[(q(x),q(y)) for x,y in pts]; d.line(p,fill=rgba(c),width=q(w),joint='curve')
    r=w/2
    for x,y in (pts[0],pts[-1]): d.ellipse((q(x-r),q(y-r),q(x+r),q(y+r)),fill=rgba(c))
def rr(d,box,r=5,c=CYAN,w=2,fill=None): d.rounded_rectangle(tuple(q(x) for x in box),radius=q(r),outline=rgba(c) if c else None,width=q(w),fill=fill)
def ellipse(d,box,c=CYAN,w=2,fill=None): d.ellipse(tuple(q(x) for x in box),outline=rgba(c) if c else None,width=q(w),fill=fill)
def poly(d,pts,c=CYAN,w=2,fill=None): d.polygon([(q(x),q(y)) for x,y in pts],outline=rgba(c) if c else None,fill=fill,width=q(w))
def save(im,path):
    im=im.resize((im.width//S,im.height//S),Image.Resampling.LANCZOS)
    im.save(path,format='PNG',optimize=True)
def mark(size=128,alpha=255):
    im=canvas(size,size); d=ImageDraw.Draw(im); c=size/2
    glyph=canvas(size,size); g=ImageDraw.Draw(glyph)
    pts=[(c-size*.19,c-size*.25),(c+size*.02,c-size*.25),(c+size*.20,c-size*.12),(c+size*.24,c),(c+size*.20,c+size*.13),(c+size*.02,c+size*.25),(c-size*.19,c+size*.25)]
    ln(g,pts,BLUE,size*.038); ln(g,[(c-size*.19,c-size*.25),(c-size*.19,c+size*.25)],CYAN,size*.038)
    ln(g,[(c-size*.04,c-size*.11),(c+size*.10,c),(c-size*.04,c+size*.11)],PALE,size*.025)
    glow=glyph.getchannel('A').filter(ImageFilter.GaussianBlur(q(size*.025)));
    halo=Image.new('RGBA',im.size,rgba(BLUE,130)); halo.putalpha(glow.point(lambda a:a*130//255)); im.alpha_composite(halo); im.alpha_composite(glyph)
    d=ImageDraw.Draw(im); ellipse(d,(c+size*.20,c-size*.29,c+size*.25,c-size*.24),None,0,rgba(CYAN))
    if alpha!=255: im.putalpha(im.getchannel('A').point(lambda a:a*alpha//255))
    return im

def app_icon():
    im=canvas(256,256); px=im.load()
    for y in range(256*S):
        t=y/(256*S-1); col=tuple(round(a*(1-t)+b*t) for a,b in zip((10,29,58),(5,13,31)))+(255,)
        for x in range(256*S): px[x,y]=col
    mask=Image.new('L',im.size,0); ImageDraw.Draw(mask).rounded_rectangle((0,0,im.width-1,im.height-1),radius=q(38),fill=255); im.putalpha(mask)
    d=ImageDraw.Draw(im); d.polygon([(128,25),(216,76),(216,178),(128,231),(40,178),(40,76)],outline=rgba((25,75,133),200),width=q(2))
    im.alpha_composite(mark(190),(q(33),q(33))); d=ImageDraw.Draw(im)
    rr(d,(57,226,199,230),2,BLUE,0,rgba(BLUE,130)); rr(d,(57,226,118,230),2,CYAN,0,rgba(CYAN,230))
    save(im,OUT/'textures/icon.png')

app_icon(); save(mark(128),OUT/'textures/ai.derco.png'); save(mark(128,34),OUT/'textures/clickgui/derco_water.png')
ico=canvas(128); idraw=ImageDraw.Draw(ico); rr(idraw,(2,2,126,126),25,(24,82,142),2,rgba(NAVY,255)); ico.alpha_composite(mark(100),(q(14),q(14))); save(ico,OUT/'textures/clickgui/derco_ico.png')
# Wordmark composition; use DejaVu when present, with a portable bitmap fallback.
try: ft=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf',q(54)); fr=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',q(54))
except OSError: ft=fr=ImageFont.load_default()
im=canvas(590,170); im.alpha_composite(mark(130),(q(8),q(18))); d=ImageDraw.Draw(im)
d.text((q(154),q(39)),'Derco',font=ft,fill=rgba((235,247,255))); x=d.textbbox((q(154),q(39)),'Derco',font=ft)[2]+q(8); d.text((x,q(39)),'Visuals',font=fr,fill=rgba(CYAN))
rr(d,(155,122,560,126),2,(20,70,120),0,rgba((20,70,120),180)); rr(d,(155,122,310,126),2,BLUE,0,rgba(BLUE,240))
save(im,OUT/'textures/derco_logo.png')
# Account/profile modal art: deliberate blank content slots, no user data or fake sign-in fields.
im=canvas(380,215); d=ImageDraw.Draw(im)
for y in range(215*S):
    t=y/(215*S-1); c=tuple(round(a*(1-t)+b*t) for a,b in zip((13,34,66),(6,15,33)))+(255,)
    ImageDraw.Draw(im).line((0,y,im.width,y),fill=c)
rr(d,(1,1,379,214),15,(39,115,196),2); rr(d,(12,12,368,45),8,(34,108,180),1,rgba((12,42,80),190))
for x in (20,28,36): ellipse(d,(x,24,x+4,28),None,0,rgba(CYAN,190))
d.text((q(52),q(19)),'PROFILE / SESSION',font=ImageFont.load_default(),fill=rgba(PALE))
ellipse(d,(28,66,96,134),BLUE,3,rgba((8,28,56))); ellipse(d,(51,78,73,100),CYAN,0,rgba(CYAN)); rr(d,(42,104,82,125),12,BLUE,0,rgba(BLUE))
for y,w in ((79,220),(105,185),(131,145)): rr(d,(118,y,118+w,y+7),4,(37,80,123),0,rgba((37,80,123),190))
rr(d,(22,174,358,176),1,(34,92,145),0,rgba((34,92,145),180)); rr(d,(22,174,125,176),1,CYAN,0,rgba(CYAN,230)); save(im,OUT/'textures/account_modal.png')

# Each icon has its own recognisable silhouette, drawn in the same electric-blue/cyan language.
def glyph(name,d):
    if name in ('visuals','effects','fullbright'):
        if name=='visuals': ellipse(d,(24,40,104,88),CYAN,4); ellipse(d,(48,29,80,99),BLUE,3); ellipse(d,(55,49,73,67),PALE,0,rgba(PALE))
        elif name=='effects': poly(d,[(64,18),(72,52),(106,64),(72,76),(64,110),(56,76),(22,64),(56,52)],BLUE,0,rgba(BLUE,220)); ellipse(d,(59,59,69,69),None,0,rgba(PALE))
        else:
            ellipse(d,(44,44,84,84),CYAN,4)
            for a in range(0,360,45):
                x1=64+math.cos(math.radians(a))*27;y1=64+math.sin(math.radians(a))*27;x2=64+math.cos(math.radians(a))*43;y2=64+math.sin(math.radians(a))*43; ln(d,[(x1,y1),(x2,y2)],BLUE,4)
    elif name in ('hud','inventory','scoreboard','hotkeys'):
        if name=='hud': rr(d,(24,29,104,89),8,CYAN,4); ln(d,[(40,101),(88,101)],BLUE,4); ln(d,[(64,89),(64,101)],CYAN,3); rr(d,(34,41,59,61),3,BLUE,3); rr(d,(68,41,94,54),3,CYAN,3)
        elif name=='inventory':
            rr(d,(25,23,103,105),8,CYAN,4)
            for y in (35,57,79):
                for x in (36,58,80): rr(d,(x,y,x+13,y+13),2,BLUE,2)
        elif name=='scoreboard':
            rr(d,(30,20,98,108),7,CYAN,4)
            for i,w in enumerate((38,27,45,31)): ln(d,[(42,39+i*16),(42+w,39+i*16)],BLUE if i%2 else CYAN,3)
        else:
            rr(d,(22,37,106,89),8,CYAN,4)
            for y in (50,69):
                for x in (33,48,63,78,93): rr(d,(x,y,x+8,y+8),2,BLUE if x%2 else CYAN,1,rgba(BLUE if x%2 else CYAN,160))
    elif name in ('utilities','cooldowns'):
        ellipse(d,(34,34,94,94),CYAN,6); ellipse(d,(52,52,76,76),BLUE,4)
        for a in range(0,360,45):
            x1=64+math.cos(math.radians(a))*32;y1=64+math.sin(math.radians(a))*32;x2=64+math.cos(math.radians(a))*43;y2=64+math.sin(math.radians(a))*43; ln(d,[(x1,y1),(x2,y2)],BLUE,4)
        if name=='cooldowns': ln(d,[(64,43),(64,64),(82,76)],PALE,4)
    elif name=='combat':
        for r,c in ((40,CYAN),(27,BLUE),(12,PALE)): ellipse(d,(64-r,64-r,64+r,64+r),c,4)
        ln(d,[(64,15),(64,30)],CYAN,3); ln(d,[(64,98),(64,113)],CYAN,3)
    elif name=='hud_target':
        ellipse(d,(24,24,104,104),CYAN,4); ellipse(d,(43,43,85,85),BLUE,4); ellipse(d,(58,58,70,70),None,0,rgba(PALE)); ln(d,[(80,43),(108,16)],CYAN,4)
    elif name=='movement' or name=='trails':
        pts=[(23,84),(42,67),(58,77),(78,45),(103,31)] if name=='movement' else [(22,85),(40,68),(56,76),(72,50),(90,58),(106,30)]
        ln(d,pts,CYAN,5)
        for x,y in pts: ellipse(d,(x-4,y-4,x+4,y+4),None,0,rgba(PALE))
        if name=='movement': poly(d,[(94,22),(111,28),(102,44)],None,0,rgba(PALE))
    elif name=='render' or name=='jump_circles':
        for r,c in ((38,CYAN),(26,BLUE),(14,PALE)): ellipse(d,(64-r,64-r,64+r,64+r),c,3)
        if name=='jump_circles': ln(d,[(64,87),(64,38)],CYAN,4); poly(d,[(54,49),(64,32),(74,49)],None,0,rgba(PALE))
    elif name=='player' or name=='armor':
        if name=='player': ellipse(d,(49,22,79,52),CYAN,4); ln(d,[(28,102),(28,81),(41,61),(87,61),(100,81),(100,102)],BLUE,5); ln(d,[(28,102),(100,102)],CYAN,4)
        else: ln(d,[(64,22),(96,37),(91,78),(64,107),(37,78),(32,37),(64,22)],CYAN,5); ln(d,[(64,35),(64,88)],BLUE,4)
    elif name=='world':
        ellipse(d,(24,24,104,104),CYAN,4); ellipse(d,(45,24,83,104),BLUE,3); ln(d,[(27,51),(101,51)],BLUE,3); ln(d,[(27,77),(101,77)],BLUE,3)
    elif name=='module_world':
        ln(d,[(22,82),(43,58),(60,72),(80,39),(106,69)],CYAN,4); ln(d,[(24,91),(47,84),(70,91),(103,84)],BLUE,3); ellipse(d,(82,19,96,33),None,0,rgba(PALE))
    elif name=='watermark':
        poly(d,[(64,24),(92,40),(92,82),(64,104),(36,82),(36,40)],BLUE,3); ln(d,[(48,44),(65,44),(82,55),(84,65),(82,76),(65,88),(48,88)],CYAN,4); ln(d,[(48,44),(48,88)],CYAN,4)
    elif name=='misc' or name=='particles':
        if name=='misc': ln(d,[(40,40),(88,40),(64,88),(40,40)],CYAN,4); ln(d,[(40,40),(64,64),(88,40)],BLUE,3); ln(d,[(64,64),(64,88)],CYAN,3)
        else:
            for x,y,r,c in ((64,64,8,PALE),(37,45,6,CYAN),(90,39,5,BLUE),(88,88,7,CYAN),(34,91,4,BLUE),(61,28,4,CYAN)): ellipse(d,(x-r,y-r,x+r,y+r),None,0,rgba(c))
    elif name=='bossbar': rr(d,(22,47,106,80),12,CYAN,4); rr(d,(29,54,80,73),8,BLUE,0,rgba(BLUE,220)); poly(d,[(95,31),(105,42),(95,53),(85,42)],None,0,rgba(PALE))
    elif name=='potions': rr(d,(51,18,77,38),5,CYAN,3); ln(d,[(51,38),(42,53),(42,91),(53,104),(75,104),(86,91),(86,53),(77,38)],CYAN,4); poly(d,[(43,73),(85,73),(85,91),(74,101),(54,101),(43,91)],BLUE,0,rgba(BLUE,180))
    elif name=='saturation': ellipse(d,(27,25,101,101),CYAN,4); poly(d,[(64,39),(47,66),(51,83),(64,94),(77,83),(81,66)],BLUE,0,rgba(BLUE,220))
    elif name=='totems': rr(d,(40,21,88,107),8,CYAN,4); ellipse(d,(52,35,76,59),PALE,4); ln(d,[(64,64),(64,92)],BLUE,4); ln(d,[(49,76),(79,76)],CYAN,3)
    elif name=='tnt_timer': rr(d,(31,50,97,99),7,CYAN,4); rr(d,(42,62,86,86),2,BLUE,0,rgba(BLUE,150)); ln(d,[(51,48),(51,38),(70,32),(70,48)],PALE,3); ellipse(d,(68,21,78,31),None,0,rgba(CYAN))
    elif name=='crosshair':
        for pts,c in [([(28,43),(28,28),(43,28)],CYAN), ([(85,28),(100,28),(100,43)],BLUE), ([(28,85),(28,100),(43,100)],BLUE), ([(85,100),(100,100),(100,85)],CYAN)]: ln(d,pts,c,5)
        ln(d,[(64,42),(64,58)],PALE,3); ln(d,[(64,70),(64,87)],CYAN,3); ln(d,[(42,64),(58,64)],BLUE,3); ln(d,[(70,64),(87,64)],BLUE,3)
    elif name=='hitbox': ln(d,[(39,34),(82,34),(101,50),(59,50),(39,34)],CYAN,3); ln(d,[(39,34),(39,83),(59,100),(59,50)],BLUE,4); ln(d,[(59,100),(101,83),(101,50)],CYAN,3); ln(d,[(82,34),(82,83)],BLUE,3)
    elif name=='zoom': ellipse(d,(22,22,83,83),CYAN,6); ln(d,[(74,74),(106,106)],BLUE,8); ln(d,[(38,52),(68,52)],PALE,3)
    elif name=='sprint': ln(d,[(24,86),(47,68),(70,54),(101,34)],CYAN,6); poly(d,[(88,27),(109,28),(100,48)],None,0,rgba(PALE))
    elif name=='elytra': ln(d,[(64,30),(64,98)],PALE,4); ln(d,[(64,41),(37,27),(24,44),(43,88),(64,75),(85,88),(104,44),(91,27),(64,41)],CYAN,4)
    elif name=='sound': poly(d,[(27,53),(43,53),(60,38),(60,90),(43,75),(27,75)],CYAN,4); [d.arc((q(48-r),q(64-r),q(48+r),q(64+r)),305,55,fill=rgba(BLUE),width=q(3)) for r in (18,31,43)]
    elif name=='inventory_sort':
        for x,y in ((30,30),(68,30),(30,68),(68,68)): rr(d,(x,y,x+29,y+29),4,CYAN if x==30 else BLUE,3)
        ln(d,[(43,44),(54,44)],PALE,3); ln(d,[(43,82),(54,82)],PALE,3)

def icon(name,path):
    im=canvas(128); d=ImageDraw.Draw(im)
    rr(d,(8,8,120,120),24,(35,94,153),2)
    d.arc((q(13),q(13),q(115),q(115)),195,290,fill=rgba(CYAN,150),width=q(2))
    for x,y in ((22,22),(106,22),(22,106),(106,106)): ellipse(d,(x-2,y-2,x+2,y+2),None,0,rgba(BLUE,210))
    layer=canvas(128); glyph(name,ImageDraw.Draw(layer)); alpha=layer.getchannel('A').filter(ImageFilter.GaussianBlur(q(4))); halo=Image.new('RGBA',layer.size,rgba(BLUE,85)); halo.putalpha(alpha.point(lambda a:a*85//255)); im.alpha_composite(halo); im.alpha_composite(layer)
    save(im,path)

sets={
'categories':['visuals','hud','utilities','combat','movement','render','player','world','misc'],
'hud':['armor','bossbar','cooldowns','effects','hotkeys','inventory','potions','saturation','scoreboard','target','totems','watermark','tnt_timer'],
'modules':['crosshair','fullbright','trails','particles','hitbox','jump_circles','zoom','sprint','elytra','world','sound','inventory_sort']}
for group,names in sets.items():
    for name in names:
        variant = 'module_world' if group=='modules' and name=='world' else ('hud_target' if group=='hud' and name=='target' else name)
        icon(variant,OUT/'textures'/group/(name+'.png'))

# Selected-module header artwork and inspector accent are intentionally different assets.
im=canvas(310,65); d=ImageDraw.Draw(im); rr(d,(1,1,309,64),10,(35,117,195),2); rr(d,(8,8,12,57),2,CYAN,0,rgba(CYAN,210))
for i in range(5): ln(d,[(255+i*10,13),(275+i*10,13)],BLUE,1); ellipse(d,(260+i*10,28,264+i*10,32),None,0,rgba(CYAN,135))
ln(d,[(18,52),(125,52)],BLUE,2); ln(d,[(18,52),(58,52)],CYAN,2); save(im,OUT/'textures/panels/selected_module.png')
im=canvas(256,64); d=ImageDraw.Draw(im); rr(d,(1,1,255,63),9,(34,113,192),2); ln(d,[(16,48),(36,28),(56,48),(76,28)],CYAN,2); rr(d,(102,22,226,26),2,BLUE,0,rgba(BLUE,120)); rr(d,(102,34,192,38),2,CYAN,0,rgba(CYAN,100)); save(im,OUT/'textures/panels/detail_accent.png')

manifest={
 'brand':['textures/icon.png','textures/ai.derco.png','textures/derco_logo.png','textures/clickgui/derco_water.png','textures/clickgui/derco_ico.png','textures/account_modal.png'],
 'categories':[f'textures/categories/{x}.png' for x in sets['categories']],
 'hud':[f'textures/hud/{x}.png' for x in sets['hud']],
 'modules':[f'textures/modules/{x}.png' for x in sets['modules']],
 'panels':['textures/panels/selected_module.png','textures/panels/detail_accent.png']}
(OUT/'asset-manifest.json').write_text(json.dumps(manifest,indent=2)+'\n',encoding='utf-8')
print('Created',len(list((OUT/'textures').rglob('*.png'))),'PNG assets under',OUT)

