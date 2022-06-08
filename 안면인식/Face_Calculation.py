import Face_Function

import Doorlock_Move

import Firebase_Main

import cv2

import numpy as np

from datetime import datetime



def MyFace(face_d,img_count):
    
  now = datetime.now()
  
  #얼굴 인식 및 랜드마크 인식을 위한 선언
  predictor_path=Face_Function.Face_Function.predictor_path
  
  face_recog= Face_Function.Face_Function.face_recog

  detector= Face_Function.Face_Function.detector
  
  predictor = Face_Function.Face_Function.predictor
    
  count=0
  
  # facefolder에 저장된 사진 개수만큼
  for a in range(len(img_count)):
      
      # a번째 사진 img 에 선언
      img = cv2.imread("facefolder/{}".format(img_count[a]))
      
      #해상도 지정
      img = cv2.resize(img,dsize=(640,480))
      
      #사진에서 얼굴 인식
      dets=detector(img,0)
      
      #얼굴 인식 개수 만큼 성공시
      for face in dets :
        
        #사진 얼굴에 랜드마크
        shape = predictor(img, face)
        
        face_descriptor = face_recog.compute_face_descriptor(img,shape)
        #사진 얼굴에 랜드마크 좌표값 저장
        face_descriptors = np.array(face_descriptor)
        
        #카메라에서 인식한 얼굴 랜드마크(face_d)를 x 에 저장
        x=np.array([face_d])
        
        #사진에서 인식한 얼굴 랜드마크(face_descriptors)를 y에 저장
        y=np.array([face_descriptors])
        
        #사진 , 카메라 얼굴 좌표 벡터값을 numpy 유클리디안거리 계산  (유사도)
        dist = np.linalg.norm(x-y, axis=1)
        
        #유사도 출력
        print(dist)
        
        #어떤 사진인지 이름 출력
        print(img_count[a])
        
        #유사도(dist) 0.4 미만이면 (즉 60%이상이면) 일치된 얼굴 같은 얼굴
        if dist <= 0.4:
          
          print('allow face')
          
          #일치할시 현재시간과 이름 파이어베이스 출입 로그 에 보내기
          data={"name":"{}_{}".format(now.strftime('%Y-%m-%d %H:%M:%S'),img_count[a])}
          
          db=Firebase_Main.Token.db
          
          db.child("OpenDoor_Log").push(data)
          
          #일치할시 도어락 작동
          Doorlock_Move.Doorlock()
          
        #불일치할시 
        if dist > 0.4:
            
          print('Wrong')
          
