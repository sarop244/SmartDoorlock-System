import dlib

#dlib 기능(얼굴 인식 , 랜드마크 출력)
class Face_Function():
    
    predictor_path = 'shape_predictor_68_face_landmarks.dat'
    
    face_recog = dlib.face_recognition_model_v1("dlib_face_recognition_resnet_model_v1.dat")

    detector = dlib.get_frontal_face_detector()
    
    predictor = dlib.shape_predictor(predictor_path)

    
    
