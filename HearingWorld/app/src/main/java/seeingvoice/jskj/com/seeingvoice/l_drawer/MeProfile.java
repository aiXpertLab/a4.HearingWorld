package seeingvoice.jskj.com.seeingvoice.l_drawer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import seeingvoice.jskj.com.seeingvoice.MyBaseActivity;
import seeingvoice.jskj.com.seeingvoice.MySP;
import seeingvoice.jskj.com.seeingvoice.R;

/**
 * @author  LeoReny@hypech.com
 * @version 1.0
 * @since   2021-04-07
 */

public class MeProfile extends MyBaseActivity implements View.OnClickListener{
    final String TAG = this.getClass().getSimpleName();
    private static final int UPDATE_AVATAR_BY_ALBUM = 2;
    private static final int UPDATE_USER_NICK_NAME = 3;

    MySP aSPUser = MySP.getInstance();
    View mLayout;

    TextView textViewTitle, textViewNickName;
    ShapeableImageView viewImageAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_frag3_me_profile);
        mLayout = findViewById(R.id.a_frag3_me_profile);

        RelativeLayout rAvatar  = findViewById(R.id.relative_layout_avatar);
        RelativeLayout rNickName= findViewById(R.id.rl_nick_name);
        RelativeLayout rQrCode  = findViewById(R.id.rl_qr_code);
        RelativeLayout rMore    = findViewById(R.id.relative_layout_more);

        textViewTitle   = findViewById(R.id.text_view_title);
        textViewNickName= findViewById(R.id.text_view_nick_name);
        viewImageAvatar = findViewById(R.id.iv_avatar);

        rAvatar.setOnClickListener(this);       //头像行
        rNickName.setOnClickListener(this);     //昵称行
        textViewNickName.setOnClickListener(this);   //昵称
        rQrCode.setOnClickListener(this);
        rMore.setOnClickListener(this);       //more

        initView();
    }

    private void initView() {
        TextPaint paint = textViewTitle.getPaint();
        paint.setFakeBoldText(true);

        textViewNickName.setText(MySP.getInstance().getUNickName());

        String userAvatar = MySP.getInstance().getUAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            viewImageAvatar.setImageURI(Uri.parse(userAvatar));
        }
    }

    private void showExplanation(String title, String message, final String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });
        builder.create().show();
    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Toast.makeText(this, getString(R.string.request_permission_granted_storage), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.permission_rationale_storage), Toast.LENGTH_LONG).show();
        }
    });


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.relative_layout_avatar){
            //permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Intent intent;
                intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, UPDATE_AVATAR_BY_ALBUM);
            }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showExplanation(getString(R.string.request_permission_title), getString(R.string.permission_rationale_storage), Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else if((v.getId() == R.id.rl_nick_name) || (v.getId() == R.id.text_view_nick_name)) {
            startActivityForResult(new Intent(this, ChangeName.class), UPDATE_USER_NICK_NAME);
        }else if(v.getId() == R.id.rl_qr_code){
            Intent intent = new Intent(MeProfile.this, SVQRCode.class);
            intent.putExtra("from", "Frag3MeProfile");
            startActivity(intent);
        }else if(v.getId() == R.id.relative_layout_more){
            startActivity(new Intent(this, MeMore.class));
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_AVATAR_BY_ALBUM:
                    if (data != null) {
                        Uri uri = data.getData();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MySP.getInstance().setUAvatar(String.valueOf(uri));
                            }
                        }).start();
                        viewImageAvatar.setImageURI(uri);
                    }
                    break;
                case UPDATE_USER_NICK_NAME:
                    this.textViewNickName.setText(aSPUser.getUNickName());

                    Snackbar sb = Snackbar.make(mLayout.getRootView(), getString(R.string.modify_nick_name_success_tips), Snackbar.LENGTH_SHORT);
                    sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.wechat_btn_green));
                    sb.setAnchorView(findViewById(R.id.view_nick));
                    sb.show();
                    break;
            }
        }
    }
}