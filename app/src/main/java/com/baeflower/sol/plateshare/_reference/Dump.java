package com.baeflower.sol.plateshare._reference;

/**
 * Created by sol on 2015-06-04.
 */
public class Dump {

    private void makeIntro1() {
        /*
        // 1. onCreate()
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 2000); // Runnable 객체를 delayMillis 시간 후에 실행해라
        */


        /*
        // onDestory()
        // 어플리케이션 종료할 때 실제 onDestroy() 메서드가 호출되어도,
        // 메모리 상에서 Handler 가 PostDelayed 메서드를 실행시켜 어플리케이션이 다시 실행되는 경우를 막기 위해서
        // 항상 어플리케이션이 종료될 경우를 대비해서 removeCallBacks(Runnable r) 을 해주는 것이 좋다

        // mHandler.removeCallbacks(mRunnable);
         */
    }

    // 한 activity 안에서 하는 것. 근데 동적으로 테마가 바뀌지 않아서 안씀
    private void makeIntro2() {

        /*
        // 1. onCreate()
        boolean isRunIntro = getIntent().getBooleanExtra("intro", true);
        if(isRunIntro) {
            beforeIntro();
        } else {
            afterIntro(savedInstanceState);
        }
        */

        /*
           // 인트로 화면
            private void beforeIntro() {
                Log.d(TAG, "beforeIntro");

                // 약 2초간 인트로 화면을 출력.
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("intro", false);
                        startActivity(intent);
                        // 액티비티 이동시 페이드인/아웃 효과를 보여준다. 즉, 인트로
                        //    화면에 부드럽게 사라진다.
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }, 3000);
            }

            // 인트로 화면 이후
            private void afterIntro(Bundle savedInstanceState) {
                Log.d(TAG, "afterIntro");

                // 기본 테마를 지정한다
                // 동적으로
                setTheme(R.style.AppTheme);
                setContentView(R.layout.activity_intro);
            }
         */
    }


}
