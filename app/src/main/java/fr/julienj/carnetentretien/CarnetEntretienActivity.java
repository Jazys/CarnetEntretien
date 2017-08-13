package fr.julienj.carnetentretien;

/**

 Dans le menu scan :
  - soit on scan et ça affiche l'eqpt
  - soit on a une liste avec les divers equipements

 Dans le menu ajouter
  - Enregistrer en database les données
  - choisir un modèle pour l'édition
  - enregistrements des rappels calendrier

 Dans le menu voir :
  - liste l'ensemble des eqpt
  - faire une fiche pour visualiser


 Dans le menu parametre:
  - configuration pour extraire les données
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import fr.julienj.carnetentretien.fragment.AddEquipment;

public class CarnetEntretienActivity extends AppCompatActivity {

    private android.support.v4.app.FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;

    private AddEquipment fraAddEqpt;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_scan:


                    return true;
                case R.id.navigation_add:

                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fraAddEqpt = AddEquipment.newInstance(R.layout.fragment_add_eqpt);
                    fragmentTransaction.replace(R.id.content,fraAddEqpt);
                    fragmentTransaction.commit();

                    return true;
                case R.id.navigation_view:

                    return true;
                case R.id.navigation_params:

                    return true;
            }
            return false;
        }

    };

    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch(what) {
                case 0:

                    break;
                case 1:

                    break;
            }
        }
    };
    //call myHandler.sendEmptyMessage(XX); to enter in the condition above

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carnet_entretien);

        //event liée au menu
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        init();
    }

    private void init()
    {
        //Pour demander les permissions GPS
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //User permission needed
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        }

    }


    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(CarnetEntretienActivity.this, toast, Toast.LENGTH_SHORT).show();



            }
        });
    }

}
