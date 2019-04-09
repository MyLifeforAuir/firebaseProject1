package com.example.firebaseproject1;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class omok extends AppCompatActivity implements OnClickListener {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference docRef = db.collection("users").document(player.getUid());
        TextView user;
        TextView computer;
        TextView result;

        TextView winRateText, winText, loseText;
        int win, lose;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_omok);
            displayUserInfo();
            winRateText = findViewById(R.id.winRateTextView);
            winText=findViewById(R.id.winTextView);
            loseText=findViewById(R.id.loseTextView);
            updateWinLose();
            user = (TextView)findViewById(R.id.textUser);
            computer = (TextView)findViewById(R.id.textComputer);
            result = (TextView)findViewById(R.id.textResult);
            Button buttonA = (Button)findViewById(R.id.buttonA);
            Button buttonB = (Button)findViewById(R.id.buttonB);
            Button buttonC = (Button)findViewById(R.id.buttonC);
            buttonA.setOnClickListener(this);
            buttonB.setOnClickListener(this);
            buttonC.setOnClickListener(this);

        }

        /*@Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            //getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }*/

        @Override
        public void onClick(View v) {
            String comX = "";           // 컴퓨터가 낸 걸 보관하는 장소
            String userX = "";          // 사람이 낸 걸 보관하는 장소
            String resultX = "";        // 승패 결과를 보관하는 장소

            // 컴퓨터 가위 바위 보 선택
            Random r = new Random();
            float f = r.nextFloat();
            if(f<0.333){
                comX = "가위";
            } else if (f<0.666){
                comX = "바위";
            } else {
                comX = "보";
            }
            computer.setText(comX);     // 컴퓨터가 낸 걸 화면에 표시

            // 사람 가위 바위 보 선택 (누른 버튼에 따라)
            switch(v.getId()){
                case R.id.buttonA:
                    userX = "가위";
                    break;
                case R.id.buttonB:
                    userX = "바위";
                    break;
                case R.id.buttonC:
                    userX = "보";
                    break;
            }
            user.setText(userX);    // 사람이 낸 걸 화면에 표시

            // 컴퓨터랑 사람의 승패 결정
            if(userX.equals("가위")){     // 사람이 가위를 낸 경우
                if(comX.equals("가위"))
                    resultX = "비겼어요!!";
                else if (comX.equals("바위")) {
                    resultX = "컴퓨터 승리!!";
                lose();
                }
                else {      // 컴퓨터가 보를 낸 경우
                    resultX = "플레이어 승리!!";
                    win();
                }updateWinLose();
            } else if(userX.equals("바위")){  // 사람이 바위를 낸 경우
                if(comX.equals("가위")) {
                    resultX = "플레이어 승리!!";
                    win();
                }
                else if (comX.equals("바위"))
                    resultX = "비겼어요!!";
                else {       // 컴퓨터가 보를 낸 경우
                    resultX = "컴퓨터 승리!!";
                    lose();
                }
                updateWinLose();
            } else if(userX.equals("보")){       // 사람이 보를 낸 경우
                if(comX.equals("가위")) {
                    resultX = "컴퓨터 승리!!";
                    lose();
                }
                else if (comX.equals("바위")) {
                    resultX = "플레이어 승리!!";
                    win();
                }
                else        // 컴퓨터가 보를 낸 경우
                    resultX = "비겼어요!!";
            }updateWinLose();
            result.setText(resultX);    // 승패 결과를 화면에 표시
        }

    void displayUserInfo(){
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("firestore", "DocumentSnapshot data: " + document.getData().get("Win")+"  "+document.getData().get("Lose"));
                        Long  longwin= (Long)(document.getData().get("Win"));
                        Long longlose= (Long)(document.getData().get("Lose"));

                        win=longwin.intValue();
                        lose=longlose.intValue();
                    } else {
                        onBackPressed();
                        Log.d("firestore", "No such document");
                    }
                } else {
                    onBackPressed();
                    Log.d("firestore", "get failed with ", task.getException());
                }
            }
        });


    }
    //이겼을시 db에 승리1 추가
    void win(){
       win++;
        DocumentReference Ref = db.collection("users").document(player.getUid());


        Ref
                .update("Win", win)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firestore", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firestore", "Error updating document", e);
                    }
                });

    }
    //졌을시 db에 패배 1 추가
    void lose(){
        lose++;
        DocumentReference Ref = db.collection("users").document(player.getUid());


        Ref
                .update("Lose", lose)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firestore", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firestore", "Error updating document", e);
                    }
                });

    }
    //승, 패 업데이트
    void updateWinLose(){
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                       // Log.d("firestore", "DocumentSnapshot data: " + document.getData().get("Win")+"  "+document.getData().get("Lose"));
                        Long  longwin= (Long)(document.getData().get("Win"));
                        Long longlose= (Long)(document.getData().get("Lose"));

                        win=longwin.intValue();
                        lose=longlose.intValue();

                        winText.setText("Win = "+win);
                        loseText.setText("Lose = "+lose);
                        Float winrate = ((float)win/(win+lose)*100);
                        winRateText.setText("WinRate = "+ String.format("%.2f", winrate));
                    } else {
                        onBackPressed();
                        Log.d("firestore", "No such document");
                    }
                } else {
                    onBackPressed();
                    Log.d("firestore", "get failed with ", task.getException());
                }
            }
        });

    }
}
