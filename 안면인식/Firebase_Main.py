import pyrebase

#파이어베이스 연동을 위한 토큰
class Token():
    
    config = {  "apiKey": "AIzaSyBTGrklWDyt2cWXL3XVgV7AvJLeQXX60iQ",
                
        "authDomain": "smart-doorlock-c9e00.firebaseapp.com",
                
        "databaseURL": "https://smart-doorlock-c9e00-default-rtdb.firebaseio.com",
                
        "projectId": "smart-doorlock-c9e00",
                
        "storageBucket": "smart-doorlock-c9e00.appspot.com",
                
        "messagingSenderId": "404335392148",
                
        "appId": "1:404335392148:web:75c6d6f8572fea4dbf4b81",
                
        "measurementId": "G-7TB1L2YDF4",
                
        "serviceAccount": "smart-doorlock-c9e00-firebase-adminsdk-qge67-165a3f65b6.json"}

    #파이어베이스 기능들을 사용하기위한 선언
    firebase = pyrebase.initialize_app(config)
    
    auth = firebase.auth()
    
    storage = firebase.storage()
    
    db=firebase.database()

#파이어베이스에 접근에 storage에서 사진들을 다운로드
def Filedown(cnt):
         
         #storage개수만큼
         all_files = Token.storage.child().list_files()
         
         print('Firebase Image Downloading~')
         
         for file in all_files:
            
            file.download_to_filename("facefolder/{}".format(file.name))
            
            cnt+=1
            
