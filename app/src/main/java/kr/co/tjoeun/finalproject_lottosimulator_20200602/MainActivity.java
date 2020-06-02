package kr.co.tjoeun.finalproject_lottosimulator_20200602;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.tjoeun.finalproject_lottosimulator_20200602.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;

    int[] winLottoNumArr = new int[6];
    int bonusNum = 0;

    List<TextView> winNumTxts = new ArrayList<>();

    long useMoney = 0L;

    long winMoney = 0L;

    int firstRankCount = 0;
    int secondRankCount = 0;
    int thirdRankCount = 0;
    int fourthRankCount = 0;
    int fifthRankCount = 0;
    int unrankedCount = 0;


    List<TextView> myNumTxts = new ArrayList<>();

    boolean isAutoBuyRunning = false;

    Handler mHandler = new Handler();
    Runnable buyLottoRunnable = new Runnable() {
        @Override
        public void run() {

            if (useMoney < 10000000) {
                makeLottoWinNumbers();
                checkWinRank();
                mHandler.post(buyLottoRunnable);
            }
            else {
                Toast.makeText(mContext, "로또 구매를 종료합니다.", Toast.LENGTH_SHORT).show();
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupEvents();
        setValues();

    }

    @Override
    public void setupEvents() {

        binding.buyAutoLottoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isAutoBuyRunning) {
                    mHandler.post(buyLottoRunnable);
                    isAutoBuyRunning = true;
                    binding.buyAutoLottoBtn.setText(getResources().getString(R.string.pause_auto_buying));
                }
                else {
                    mHandler.removeCallbacks(buyLottoRunnable);
                    isAutoBuyRunning = false;
                    binding.buyAutoLottoBtn.setText(getResources().getString(R.string.resume_auto_buying));
                }


            }
        });

        binding.buyOneLottoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeLottoWinNumbers();
                checkWinRank();

            }
        });

    }

    @Override
    public void setValues() {

        winNumTxts.add(binding.winNumTxt01);
        winNumTxts.add(binding.winNumTxt02);
        winNumTxts.add(binding.winNumTxt03);
        winNumTxts.add(binding.winNumTxt04);
        winNumTxts.add(binding.winNumTxt05);
        winNumTxts.add(binding.winNumTxt06);

        myNumTxts.add(binding.myNumTxt01);
        myNumTxts.add(binding.myNumTxt02);
        myNumTxts.add(binding.myNumTxt03);
        myNumTxts.add(binding.myNumTxt04);
        myNumTxts.add(binding.myNumTxt05);
        myNumTxts.add(binding.myNumTxt06);

    }

    void checkWinRank() {
        useMoney += 1000;

        binding.useMoneyTxt.setText(String.format("%,d원", useMoney));


        int correctCount = 0;

        for (TextView myNumTxt : myNumTxts) {
            int myNum = Integer.parseInt(myNumTxt.getText().toString());

            for (int winNum : winLottoNumArr) {

                if (myNum == winNum) {
                    correctCount++;
                }

            }
        }

        if (correctCount == 6) {
            winMoney += 1300000000;
            firstRankCount++;
        }
        else if (correctCount == 5) {

            boolean isBonusNumCorrect = false;

            for (TextView myNumTxt : myNumTxts) {
                int myNum = Integer.parseInt(myNumTxt.getText().toString());

                if (myNum == bonusNum) {
                    isBonusNumCorrect = true;
                    break;
                }

            }

            if (isBonusNumCorrect) {
                winMoney += 54000000;
                secondRankCount++;
            }
            else {
                winMoney += 1450000;
                thirdRankCount++;
            }

        }
        else if (correctCount == 4) {
            winMoney += 50000;
            fourthRankCount++;
        }
        else if (correctCount == 3) {
            useMoney -= 5000;
            fifthRankCount++;
        }
        else {
            unrankedCount++;
        }


        binding.winMoneyTxt.setText(String.format("%,d원", winMoney));
        binding.useMoneyTxt.setText(String.format("%,d원", useMoney));


        binding.firstRankTxt.setText(String.format("%,d회", firstRankCount));
        binding.secondRankTxt.setText(String.format("%,d회", secondRankCount));
        binding.thirdRankTxt.setText(String.format("%,d회", thirdRankCount));
        binding.fourthRankTxt.setText(String.format("%,d회", fourthRankCount));
        binding.fifthRankTxt.setText(String.format("%,d회", fifthRankCount));
        binding.unrankedTxt.setText(String.format("%,d회", unrankedCount));


    }

    void makeLottoWinNumbers() {

//        지난주 당첨번호가 새 당첨번호에 영향 주는것을 막기위한 조치
//        기존 당첨번호를 모두 0 으로 세팅

        for (int i=0 ; i < winLottoNumArr.length ; i++) {
            winLottoNumArr[i] = 0;
        }

//        보너스 번호도 0으로 세팅
        bonusNum = 0;

//        당첨번호 6개를 뽑기 위한 for
        for (int i=0 ; i < winLottoNumArr.length ; i++) {

//            조건에 맞는 (중복 아닌) 숫자를 뽑을때까지 무한반복
            while (true) {

//                1~45 중 하나 랜덤
                int randomNum = (int) (Math.random() * 45 + 1);

//                중복검사 결과 저장 변수 => 일단 맞다고 했다가, 수틀리면 false 변경
                boolean isDuplicatedOk = true;

//                당첨번호중 같은게 있다면 false로 변경.
//                한번도 같은게 없었다면, true로 유지
                for (int num : winLottoNumArr) {
                    if (num == randomNum) {
                        isDuplicatedOk = false;
                        break;
                    }
                }

//                중복 검사가 통과되었다면
                if (isDuplicatedOk) {
//                    당첨번호로 등록
                    winLottoNumArr[i] = randomNum;
//                    무한반복 탈출 => 다음 당첨번호 뽑으러 감. (for문 다음 i)
                    break;
                }
            }

        }


//        6개를 뽑는 for문이 다 돌고 나면 => 순서가 뒤죽박죽.
//        Arrays 클래스의 static메쏘드 활용해서 오름차순 정렬.
        Arrays.sort(winLottoNumArr);


//        보너스번호를 뽑는 무한반복. => 1개만 뽑는다. for문은 없고, 바로 무한반복
        while (true) {

//            1~45를 랜덤 추출
            int randomNum = (int) (Math.random() * 45 + 1);

//            중복검사 진행 (앞과 같은 로직)
            boolean isDuplicatedOk = true;

            for (int num : winLottoNumArr) {
                if (num == randomNum) {
                    isDuplicatedOk = false;
                    break;
                }
            }

            if (isDuplicatedOk) {
                bonusNum = randomNum;
                break;
            }
        }


//        당첨번호들을 텍스트뷰에 표시.
        for (int i=0 ; i < winNumTxts.size() ; i++) {
            int winNum = winLottoNumArr[i];

//            화면에 있는 당첨번호 텍스트뷰들을 ArrayList에 담아두고 (setValues참고) 활용.
            winNumTxts.get(i).setText(winNum+"");
        }

//        보너스번호도 화면에 표시.
        binding.bonusNumTxt.setText(bonusNum+"");

    }

}
