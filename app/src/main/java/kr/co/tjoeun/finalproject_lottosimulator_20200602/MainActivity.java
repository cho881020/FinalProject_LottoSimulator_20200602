package kr.co.tjoeun.finalproject_lottosimulator_20200602;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupEvents();
        setValues();
    }

    @Override
    public void setupEvents() {

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

    }

    void checkWinRank() {
        useMoney += 1000;

        binding.useMoneyTxt.setText(String.format("%,d원", useMoney));
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
