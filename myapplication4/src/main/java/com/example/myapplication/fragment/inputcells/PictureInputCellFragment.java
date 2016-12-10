package com.example.myapplication.fragment.inputcells;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

/**
 * Created by Administrator on 2016/12/5.
 */

public class PictureInputCellFragment extends BaseInputCellFragment {
    ImageView imageView;
    TextView labelText;
    TextView hintText;
    final int REQUESTCODE_CAMERA = 1;
    final int REQUESTCODE_ALBUM = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inputcell_picture, container);
        imageView = (ImageView) view.findViewById(R.id.image);
        labelText = (TextView) view.findViewById(R.id.label);
        hintText = (TextView) view.findViewById(R.id.hint);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageViewClicked();
            }
        });

        return view;
    }

    void onImageViewClicked() {
        String[] items = {
                "拍照",
                "相册"
        };
        new AlertDialog.Builder(getActivity())
                .setTitle(labelText.getText())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                pickFromAlbum();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    //调用照相机
    void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUESTCODE_CAMERA);
    }

    void pickFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUESTCODE_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Activity.RESULT_CANCELED) return;
        if (requestCode == REQUESTCODE_CAMERA) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bmp);
            // Log.d("camera capture",data.getDataString()) ;
            //  Toast.makeText(getActivity(),data.getDataString(),Toast.LENGTH_LONG);
        } else if (requestCode == REQUESTCODE_ALBUM) {
            //Uri dataUri =data.getData();
            //Bitmap bmp = BitmapFactory.decodeFile(data.getDataString());
            try{
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),data.getData());
                imageView.setImageBitmap(bmp);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void setLabelText(String textView) {
        this.labelText.setText(textView);
    }

    public void setHintText(String hintText) {
        this.hintText.setHint(hintText);
    }

}
