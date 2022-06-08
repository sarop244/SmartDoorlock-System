package com.example.lsyz1;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 사용자 게정 정보 모델 클래스
 */

public class UserAccount {

    public Map<String,String> setTime; //로그인시 현재 시간
    private String idToken;         // Firebase Uid (고유 토큰정보)
    private String emailId;         // 이메일 아이디
    private String password;        // 비밀번호
    private String username;            // 이름

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {this.username = username;}



    public String getSetTime() {
        long now = System.currentTimeMillis()+43200000; //현재 서버시간 + 12시간 = 한국시간
        Date mDate = new Date(); ///현재시간을 불러옴
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년-MM월-dd일 hh시 mm분 ss초"); //출력 화면
        String setTime = dateFormat.format(mDate); // setTime 안에 변경된 출력형식을 넣음
        return setTime; //
    }

    public void setSetTime(Map<String, String> setTime) {
        this.setTime = setTime;
    }
}




