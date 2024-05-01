import cv2

def parseSpriteSheet(sheetPath):
    image = cv2.imread(sheetPath)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    ret, thresh = cv2.threshold(gray,150,255,cv2.THRESH_BINARY)
    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    contours = list(filter(lambda x:cv2.contourArea(x) > 500, contours))
    cv2.drawContours(image, contours, -1, (0,255,0), 3)
    
    sequences = dict()
    rects = [cv2.boundingRect(contour) for contour in contours]
    for rect in rects:
        if rect[1] in sequences:
            sequences[rect[1]].append(((rect[0], rect[1]), (rect[0] + rect[2], rect[1] + rect[3])))
        else:
            sequences[rect[1]] = [((rect[0], rect[1]), (rect[0] + rect[2], rect[1] + rect[3]))]
    
    res = []
    for k in sorted(list(sequences.keys())):
        res.append(sorted(sequences[k], key = lambda x:x[0]))
    
    return res

def printFrame(sheetImage, bounds):
    ((x,y),(x2,y2)) = bounds
    cv2.imshow("a", sheetImage[y:y2,x:x2])
    cv2.waitKey(0)
    
if __name__ == '__main__':
    crops = parseSpriteSheet('assets/images/sheets/yukari.png')
    printFrame(cv2.imread('assets/images/sheets/yukari.png'), crops[45][0])