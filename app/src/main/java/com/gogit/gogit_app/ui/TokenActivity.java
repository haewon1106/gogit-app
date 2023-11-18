package com.gogit.gogit_app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.client.MemberRetrofitClient;
import com.gogit.gogit_app.config.Config;
import com.gogit.gogit_app.config.SessionManager;
import com.gogit.gogit_app.model.Member;
import com.gogit.gogit_app.request.MemberSignInRequest;
import com.gogit.gogit_app.service.MemberService;
import com.gogit.gogit_app.util.MyToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TokenActivity extends AppCompatActivity {
    @SuppressLint("CheckResult")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_token);

        // 세션 매니저 인스턴스
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        // 토큰이 있으면 입력받지 않음
        if (sessionManager.getToken() != null && sessionManager.getUserId() != null) {
            Intent intent = new Intent(TokenActivity.this, MainActivity.class);
            startActivity(intent);
        }

        // 레트로핏과 서비스 인스턴스
        Retrofit retrofit = MemberRetrofitClient.getRetrofitInstance();
        MemberService  memberService = retrofit.create(MemberService.class);

        // 뷰 가져오기
        Button submitButton = findViewById(R.id.submit_button);
        EditText toeknEditText = findViewById(R.id.token_editText);
        TextView errorTextView = findViewById(R.id.token_not_found);

        // 확인 버튼을 눌렀을 때 이벤트
        submitButton.setOnClickListener(e -> {
            String token = toeknEditText.getText().toString();

            // 토큰을 입력하지 않았을 때
            if (token == null || token.length() == 0) {
                errorTextView.setVisibility(View.VISIBLE);
                MyToast.showToast(getApplicationContext(), "토큰을 입력해주세요.");
                return;
            }

            // 세션 스토라지에 저장
            sessionManager.saveLoginDetails(
                    getIntent().getStringExtra(SessionManager.KEY_USERID),
                    token.trim()
            );

            Log.d("my tag", sessionManager.getToken());

            Log.d("my tag", sessionManager.getUserId());
            Log.d("my tag", sessionManager.getToken());
            // sharedPreferences의 로그인 정보가 null일 때
            if (sessionManager.getUserId() == null || sessionManager.getToken() == null) {
                MyToast.showToast(getApplicationContext(), "유저 이름과 토큰이 없습니다.");
                return;
            }


            // 리퀘스트 생성
            MemberSignInRequest memberSignInRequest = new MemberSignInRequest(
                    sessionManager.getUserId(),
                    sessionManager.getToken(),
                    getIntent().getStringExtra("avatar_url"),
                    getIntent().getStringExtra("html_url")
            );

            Log.d("my tag", memberSignInRequest.toString());


            // api 요청
            Call<Member> call = memberService.createMember(memberSignInRequest);
            call.enqueue(new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    if (response.code() == 404) {
                        Log.d("my tag", "404 error");
                    } else {
                        Member member = response.body();
                        sessionManager.setUserPk(member.getId());
                        Log.d("my tag", member.toString());

                        // 액티비티 이동
                        Intent intent = new Intent(TokenActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {
                    Log.d("my tag", "onFailure: " + t.getMessage());
                    MyToast.showToast(getApplicationContext(), "네트워크 에러 발생");
                }
            });



        });
    }
}