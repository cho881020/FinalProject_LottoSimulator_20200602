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

        for (int i=0 ; i < winLottoNumArr.length ; i++) {
            winLottoNumArr[i] = 0;
        }

        bonusNum = 0;

        for (int i=0 ; i < winLottoNumArr.length ; i++) {

            while (true) {

                int randomNum = (int) (Math.random() * 45 + 1);

                boolean isDuplicatedOk = true;
                for (int num : winLottoNumArr) {
                    if (num == randomNum) {
                        isDuplicatedOk = false;
                        break;
                    }
                }

                if (isDuplicatedOk) {
                    winLottoNumArr[i] = randomNum;
                    break;
                }
            }

        }

        Arrays.sort(winLottoNumArr);


        while (true) {

            int randomNum = (int) (Math.random() * 45 + 1);

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


        for (int i=0 ; i < winNumTxts.size() ; i++) {
            int winNum = winLottoNumArr[i];

            winNumTxts.get(i).setText(winNum+"");
        }

        binding.bonusNumTxt.setText(bonusNum+"");

    }

}
