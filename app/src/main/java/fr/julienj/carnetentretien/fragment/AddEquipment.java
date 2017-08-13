package fr.julienj.carnetentretien.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.Calendar;

import fr.julienj.carnetentretien.R;
import fr.julienj.carnetentretien.data.ContainerData;
import fr.julienj.carnetentretien.utils.HelpFunctionsUtils;

// https://stackoverflow.com/questions/12222925/set-new-layout-in-fragment aide refresh frag
public class AddEquipment extends Fragment implements View.OnClickListener  {

    private final static int TAKE_PHOTO=598;
    private Button addSimpleEqpt;
    private Button addSimpleEqptNocode;
    private Button addComplexEqpt;

    private TextView txtQrCode;
    private ImageView imgQrCode;
    private EditText nameEqpt;
    private TextView datePickerBtn;
    private Button addPhotoEqpt;
    private ImageView imgEqpt;

    private View fragmentView;

    private LayoutInflater fragmentLayout;
    private ViewGroup fragmentContainer;

    private IntentIntegrator qrScan;
    private BarcodeFormat formatScan;

    private int ressourceLayout;

    private String pathPhotoEqpt;

    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch(what) {
                case 0:

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content,AddEquipment.newInstance(R.layout.fragment_add_eqpt_content))
                            .commitAllowingStateLoss();

                    break;
                case 1:

                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static AddEquipment newInstance(int ressourceLayout) {
        Bundle bundle = new Bundle();
        bundle.putInt("ressourceLayout", ressourceLayout);

        AddEquipment fragment = new AddEquipment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            ressourceLayout = bundle.getInt("ressourceLayout");
            System.out.println("sssssssssssssssssss "+ ressourceLayout);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        readBundle(getArguments());
        View view=null;

        if(ressourceLayout==R.layout.fragment_add_eqpt) {
            view = inflater.inflate(ressourceLayout, container, false);


            fragmentView = view;
            fragmentLayout = inflater;
            fragmentContainer = container;


            addSimpleEqpt = (Button) view.findViewById(R.id.btn_add_simple_eqpt_with_qr_ean);
            addSimpleEqptNocode = (Button) view.findViewById(R.id.btn_add_simple_eqpt_without_qr_ean);
            addComplexEqpt = (Button) view.findViewById(R.id.btn_add_complex_eqpt);

            addSimpleEqpt.setOnClickListener(this);
            addSimpleEqptNocode.setOnClickListener(this);
            addComplexEqpt.setOnClickListener(this);

            init();
        }
        else {
            if (ressourceLayout == R.layout.fragment_add_eqpt_content) {

                view = inflater.inflate(R.layout.fragment_add_eqpt_content, fragmentContainer, false);

                imgQrCode = (ImageView) view.findViewById(R.id.img_scan);
                txtQrCode = (TextView) view.findViewById(R.id.txt_qrcode);
                nameEqpt = (EditText) view.findViewById(R.id.edit_nom_eqpt);
                datePickerBtn = (TextView) view.findViewById(R.id.btn_date_crea_eqpt);

                addPhotoEqpt=(Button) view.findViewById(R.id.btn_add_img_eqpt);
                imgEqpt=(ImageView) view.findViewById(R.id.img_eqpt);


                if (ContainerData.getInstance().codeScanned != null)
                    txtQrCode.setText(ContainerData.getInstance().codeScanned);

                addPhotoEqpt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
                        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        startActivityForResult( intent, TAKE_PHOTO );
                    }
                });

                datePickerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                        // TODO Auto-generated method stub
                                        // getCalender();
                                        int mYear = year;
                                        int mMonth = month;
                                        int mDay = dayOfMonth;
                                        datePickerBtn.setText(new StringBuilder()
                                                // Month is 0 based so add 1
                                                .append(mDay).append("/").append(mMonth+1).append("/")
                                                .append(mYear).append(" "));

                                    }
                                }, mYear, mMonth, mDay);
                        dialog.show();

                    }
                });

                try {

                    if (ContainerData.getInstance().codeScanned.length() == 13) {
                        formatScan = BarcodeFormat.EAN_13;
                    } else if (ContainerData.getInstance().codeScanned.length() == 8) {
                        formatScan = BarcodeFormat.EAN_8;
                    } else {

                    }

                    Bitmap bitmap = HelpFunctionsUtils.encodeAsBitmap(ContainerData.getInstance().codeScanned, BarcodeFormat.EAN_13);
                    imgQrCode.setImageBitmap(bitmap);

                    System.out.println("sssssssssssssss yes ");
                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }
        }


        return view;
    }

    private void init()
    {
        qrScan=IntentIntegrator.forSupportFragment((Fragment) AddEquipment.this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.btn_add_simple_eqpt_with_qr_ean:

                new AlertDialog.Builder(getActivity())
                        .setTitle("Message")
                        .setMessage(getActivity().getResources().getString(R.string.message_add_eqpt))
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        qrScan.initiateScan();
                                    }
                                }
                        ).show();

                break;
            case R.id.btn_add_simple_eqpt_without_qr_ean:
                break;
            case R.id.btn_add_complex_eqpt:
                showToast(getActivity().getResources().getString(R.string.no_available_funct));
                break;
            default:
                break;
        }
    }

    private void recreateViewAddSimple()
    {
        System.out.println("sssssssssssssss ");
        fragmentContainer.removeAllViews();
        fragmentView=fragmentLayout.inflate(R.layout.fragment_add_eqpt_content,fragmentContainer,false);

        ImageView imageView = (ImageView) fragmentView.findViewById(R.id.img_scan);
        try {
            Bitmap bitmap = HelpFunctionsUtils.encodeAsBitmap(ContainerData.getInstance().codeScanned,formatScan);
            imageView.setImageBitmap(bitmap);

            System.out.println("sssssssssssssss yes ");
        } catch (WriterException e) {
            e.printStackTrace();
        }

        fragmentView.invalidate();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //Getting the scan results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case TAKE_PHOTO:
                getPhotos(requestCode,resultCode, data);
                break;
            default:
                resultQrCodeEan(IntentIntegrator.parseActivityResult(requestCode, resultCode, data));
                break;
        }

    }

    private void resultQrCodeEan(IntentResult result )
    {
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                showToast("Not found");
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    formatScan=BarcodeFormat.QR_CODE;
                    ContainerData.getInstance().codeScanned=obj.toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    ContainerData.getInstance().codeScanned=result.getContents().toString();
                    formatScan=BarcodeFormat.MAXICODE;
                    showToast(ContainerData.getInstance().codeScanned);

                }

                //Maj UI
                myHandler.sendEmptyMessageAtTime(0,1000);

            }

        }
    }

    private void getPhotos(int requestCode, int resultCode, Intent data)
    {
        if (data!=null && data.hasExtra("data") ) {

                Bundle extras = data.getExtras();

                Bitmap imageBitmap = (Bitmap) extras.get("data");

                //permet de faire en sorte que l'image soit une icône
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                imageBitmap=Bitmap.createScaledBitmap(imageBitmap, 300, 300, false);
                imgEqpt.setImageBitmap(imageBitmap);

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(pathPhotoEqpt);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try{
                        out.close();
                    } catch(Throwable ignore) {}
                }

        }

    }

    public void showToast(final String toast)
    {
        getActivity().runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();

            }
        });
    }

}
