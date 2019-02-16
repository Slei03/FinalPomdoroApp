package sallycs.com.pomoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;


//Manages and initializes the fragments that build the app

public class MainActivity extends AppCompatActivity implements TaskNum{

    public int taskCompleted;

    private BottomNavigationView mainNav;
    private FrameLayout mainFrame;

    private HomeFragment homeFragment;
    private DataFragment dataFragment;
    private SettingsFragment settingsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

        mainNav = findViewById(R.id.nav_bar); //defines navigation bar
        mainFrame = findViewById(R.id.main_frame);  //creates the frame where content is loaded

        //Initializes all the fragments users can navigate between.
        homeFragment = new HomeFragment();
        dataFragment = new DataFragment();
        settingsFragment = new SettingsFragment();

        setFragment(homeFragment);
        setTab(0);

        //Determines which tab is being selected and sets content(fragment)
        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId()){

                    case (R.id.nav_home):
                        setFragment(homeFragment);
                        return true;
                    case(R.id.nav_data):
                        setFragment(dataFragment);
                        return true;
                    case(R.id.nav_settings):
                        setFragment(settingsFragment);
                        return true;

                        default:
                            return false;
                }
            }
        });
    }

    //Method sets the fragment whose content will be shown.
    void setFragment(Fragment f) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, f);
        fragmentTransaction.commit();
    }

    //Method identifies which tab is being selected.
    public void setTab(int position){
        Menu menu = mainNav.getMenu();
        MenuItem menuItem = menu.getItem(position);
        menuItem.setChecked(true);
    }


    @Override
    public void setTaskNum(int n){
        this.taskCompleted = n;
    }

    @Override
    public int getTaskNum() {
        return taskCompleted;
    }
}
