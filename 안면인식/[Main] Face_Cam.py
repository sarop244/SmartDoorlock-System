import Face_Calculation

import Face_Function

import Firebase_Main

import Doorlock_Move

import cv2

import numpy as np

import os

from math import hypot

# 눈 중앙지점
def midpoint(p1, p2):
    
    return int((p1.x + p2.x)/2), int((p1.y + p2.y)/2)

# 눈 깜빡임 감지
def get_blinking(eye_points, facial_landmarks):
    
    left_point = (facial_landmarks.part(eye_points[0]).x, facial_landmarks.part(eye_points[0]).y)

    right_point = (facial_landmarks.part(eye_points[3]).x, facial_landmarks.part(eye_points[3]).y)

    center_top = midpoint(facial_landmarks.part(eye_points[1]), facial_landmarks.part(eye_points[2]))

    center_bottom = midpoint(facial_landmarks.part(eye_points[5]), facial_landmarks.part(eye_points[4]))
 
    hor_line_lenght = hypot((left_point[0] - right_point[0]), (left_point[1] - right_point[1]))
    
    ver_line_lenght = hypot((center_top[0] - center_bottom[0]), (center_top[1] - center_bottom[1]))

    ratio = hor_line_lenght / ver_line_lenght

    return ratio

# facefolder 내 기존 이미지 삭제
def DelImage():
    
    print('FaceFolder Clear')
    
    if os.path.exists('facefolder/'):
        
        for file in os.scandir('facefolder'):
            
            os.remove(file)

# 파이어 베이스 연동을 위한 선언
firebase = Firebase_Main.Token.firebase

auth = Firebase_Main.Token.auth

storage = Firebase_Main.Token.storage

#카메라 작동
cap = cv2.VideoCapture(0)

#얼굴 인식 및 랜드마크 인식을 위해 선언
predictor_path=Face_Function.Face_Function.predictor_path

face_recog= Face_Function.Face_Function.face_recog

detector= Face_Function.Face_Function.detector

predictor = Face_Function.Face_Function.predictor

#눈 랜드마크 좌표
r_eye_points = [42, 43, 44, 45, 46, 47]

l_eye_points = [36, 37, 38, 39, 40, 41]

blinking_count=0

count=0

noface=0

db=Firebase_Main.Token.db

while(cap.isOpened()):
    #카메라 영상 지정
    ret, frame = cap.read()
    
    #해상도 지정
    frame = cv2.resize(frame,dsize=(640,480))
    
    #좌우반전
    frame = cv2.flip(frame,1)
    
    #얼굴 인식
    dets = detector(frame, 0)
    
    count%=16  
    
    count+=1
    
    noface%=100
    
    noface+=1
    
    #어플에서 '열기' 눌렀을시 작동
    asdf=db.child("문 열기").get()
    
    if(asdf.val()=="ON"):
        
        Doorlock_Move.Doorlock()
    
    #얼굴인식이 되면
    if dets:
        
        for k, d in enumerate(dets):
            
            shape = predictor(frame, d)
            
            #눈 깜빡임 감지 확인
            right_eye = get_blinking(r_eye_points, shape)
        
            left_eye = get_blinking(l_eye_points, shape)
            
            blinking = (left_eye + right_eye) / 2
            
            #눈 깜빡임 감지했을시
            if(blinking>=5.0):
                
                blinking_count+=1

            for num in range(shape.num_parts):
                
                #화면에 랜드마크 표시
                cv2.circle(frame, (shape.parts()[num].x, shape.parts()[num].y), 3, (0,255,0), -1)
                
                #눈깜빡임 감지하고 1.5초 후에
            if count==15 and blinking_count>=2 :

                face_descriptor = face_recog.compute_face_descriptor(frame,shape)
                
                #얼굴 랜드마크에 좌표 저장
                face_descriptors= np.array(face_descriptor)
                
                #다운받은 이미지 전체선언
                img_count = os.listdir('facefolder/')
                
                #얼굴과 이미지 유사도 측정을 위해
                Face_Calculation.MyFace(face_descriptors,img_count)
        
    else:
        
        #얼굴이 없을시 데이터베이스에서 이미지 다운
        if(noface==1):
            
          cnt=0
          
          blinking_count=0
          
          DelImage()
          
          #파이어베이스에서 이미지 다운
          Firebase_Main.Filedown(cnt)
            
    cv2.imshow('frame', frame)
    
    if cv2.waitKey(1) & 0xFF == ord('q'):
        
        print("q pressed")
        
        break
    
cap.release()

