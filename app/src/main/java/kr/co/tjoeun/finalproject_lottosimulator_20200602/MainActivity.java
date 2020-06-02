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

//    같은 핸들러를 여러곳에서 사용하니까 => 멤버변수로 생성. (공유하려고)
    Handler mHandler = new Handler();

//    구매 로직 코드도 => 여러곳에서 사용하려고 => 멤버변수로 생성.
    Runnable buyLottoRunnable = new Runnable() {
        @Override
        public void run() {

//            사용한 금액이 1천만원 이하라면
            if (useMoney < 10000000) {

//                로또 번호 생성 / 등수 맞추기 진행
                makeLottoWinNumbers();
                checkWinRank();

//                이 행동을 다시 할일로 등록해달라.
//                 => 반복으로 동작하게 되는 이유.
                mHandler.post(buyLottoRunnable);
            }
            else {
//                돈을 다 썼으면 로또 구매 종료 안내.
//                추가로 할일 등록 X => 반복되지 않는 이유.
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

//        자동구매를 누르면
        binding.buyAutoLottoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                지금 구매를 안돌리고 있다면
                if (!isAutoBuyRunning) {
//                    구매 시작 코드를 할일로 등록시키자. => mHandler가 실행.
                    mHandler.post(buyLottoRunnable);
//                    구매가 돌아가고 있다고 명시
                    isAutoBuyRunning = true;
//                    버튼의 문구도 중단하기로 변경
                    binding.buyAutoLottoBtn.setText(getResources().getString(R.string.pause_auto_buying));
                }
//                구매가 돌아 가고 있다면
                else {
//                    예정된 다음 구매 행동을 할일에서 제거.
//                    더 이상 할일이 없으니, 정지 된다.
                    mHandler.removeCallbacks(buyLottoRunnable);
//                    지금 구매중이 아니라고 명시.
                    isAutoBuyRunning = false;
//                    다시 누르면 재개한다고 알려줌.
//                    문구를 res / values / strings.xml 에서 가져오는 코드
                    binding.buyAutoLottoBtn.setText(getResources().getString(R.string.resume_auto_buying));
                }


            }
        });

//        한장을 구매할땐 => 로또 번호 만들고 / 등수 확인만 실행.
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

//        당첨번호 텍스트뷰들을 => ArrayList에 담아둠.
//        당첨번호를 적어줄때 편리하게 짜려고.
        winNumTxts.add(binding.winNumTxt01);
        winNumTxts.add(binding.winNumTxt02);
        winNumTxts.add(binding.winNumTxt03);
        winNumTxts.add(binding.winNumTxt04);
        winNumTxts.add(binding.winNumTxt05);
        winNumTxts.add(binding.winNumTxt06);

//        내 입력 번호도 같은 처리.
        myNumTxts.add(binding.myNumTxt01);
        myNumTxts.add(binding.myNumTxt02);
        myNumTxts.add(binding.myNumTxt03);
        myNumTxts.add(binding.myNumTxt04);
        myNumTxts.add(binding.myNumTxt05);
        myNumTxts.add(binding.myNumTxt06);

    }


//    등수 확인 코드
    void checkWinRank() {

//        사용금액은 무조건 1000원 증가
        useMoney += 1000;

//        증가된 사용금액을 화면에 반영 (중복 코드)
        binding.useMoneyTxt.setText(String.format("%,d원", useMoney));


//        맞춘 갯수 저장 변수
        int correctCount = 0;

//        내 입력 번호가 적힌 텍스트뷰들 (setValues 참고)을 꺼내봄.
        for (TextView myNumTxt : myNumTxts) {

//            적혀있는 숫자 (String)를 int로 변경
            int myNum = Integer.parseInt(myNumTxt.getText().toString());

//            내 숫자를 들고 => 당첨번호를 돌면서 확인
            for (int winNum : winLottoNumArr) {

//                같은걸 찾았다면, 맞춘 갯수를 1개 증가
                if (myNum == winNum) {
                    correctCount++;
                }

            }
        }


//        맞춘 갯수에 따른 등수 판정 + 당첨금액 누적
        if (correctCount == 6) {
            winMoney += 1300000000;
            firstRankCount++;
        }
        else if (correctCount == 5) {

//            5개를 맞췄을땐, 보너스번호 여부에 따라 2/3등 갈림.
            boolean isBonusNumCorrect = false;

//            내 입력 번호 텍스트뷰 목록을 돌면서 확인
            for (TextView myNumTxt : myNumTxts) {
//                텍스트뷰에 적힌 내용을 int로 변경
                int myNum = Integer.parseInt(myNumTxt.getText().toString());

//                보너스 번호와 비교해서, 같은게 있다면 보너스를 맞췄다고 처리.
//                한번도 이 분기에 못들어왔다 => 보너스 못맞춤.
                if (myNum == bonusNum) {
                    isBonusNumCorrect = true;
                    break;
                }

            }

//            보너스 맞추면 2등, 아니면 3등
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
//            5등은 돈으로 가져가지 않고, 로또 5장 추가 구매.
//            사용 금액을 5000원 줄여준다. => or 당첨금액을 5천원 증가로 해도 됨.
            useMoney -= 5000;
            fifthRankCount++;
        }
        else {
//            3개도 못맞췄다면 전부 낙첨 처리.
            unrankedCount++;
        }


//        사용 금액 / 당첨 금액 화면에 표시
        binding.winMoneyTxt.setText(String.format("%,d원", winMoney));
        binding.useMoneyTxt.setText(String.format("%,d원", useMoney));


//        각 등수별 (낙첨 포함) 횟수를 화면에 표시
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
