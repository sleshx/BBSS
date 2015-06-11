package sg.gov.msf.bbss.logic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.task.FileUploadTask;

/**
 * Created by bandaray on 8/4/2015.
 */
public class SupportingDocumentsHelper {

    private View rootView;
    private Context context;
    private LayoutInflater inflater;

    private Map<String,Bitmap> map = new HashMap<String, Bitmap>();
    private List<Map<String,Bitmap>> image = new ArrayList<Map<String,Bitmap>>();

    public SupportingDocumentsHelper(View rootView, Context context, LayoutInflater inflater) {
        this.rootView = rootView;
        this.context = context;
        this.inflater = inflater;
    }

    public void generateUploadFileList(Intent data, boolean isDeleteButtonRequired,
                                       MasterDataListener<ServerResponse> fileUploadListener) {
        Log.i(getClass().getName() , "----------getBitmapImageMap()");

        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        System.out.print(selectedImage.toString()+filePathColumns[0]);

        Uri uri = data.getData();
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();

        String imgNo = cursor.getString(0); // the number of the picture
        String imgPath = cursor.getString(1); // the path of the picture file
        String imgSize = cursor.getString(2); // the size of the picture
        String imgName = cursor.getString(3); // the filename of the picture
        cursor.close();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 10;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
        Map<String,Bitmap> imageMap = new HashMap<String, Bitmap>();

        imageMap.put(imgName,bitmap);
        image.add(imageMap);
        map.put(imgName,bitmap);

        uploadFile(imgPath, imgName, isDeleteButtonRequired, fileUploadListener);
    }

    public int getAttachedDocCount() {
        return ((LinearLayout)rootView.findViewById(R.id.document_listing_section)).getChildCount();
    }

    public void addDocumentItemView(final String fileName, boolean isShowDeleteButton) {
        Log.i(getClass().getName() , "----------addDocumentItemView()");

        final LinearLayout llDocListing = (LinearLayout)rootView.findViewById(
                R.id.document_listing_section);
        final RelativeLayout rlDocList = (RelativeLayout) inflater.inflate(
                R.layout.layout_supporting_doc_item, null);

        ((TextView)rlDocList.findViewById(R.id.tvDocName)).setText(fileName);
        llDocListing.addView(rlDocList);

        setDeleteDocButtonClick(llDocListing, isShowDeleteButton, rlDocList, fileName);
    }

    public void removeDocumentItemsView() {
        Log.i(getClass().getName() , "----------removeDocumentItemView()");

        ((LinearLayout)rootView.findViewById(R.id.document_listing_section)).removeAllViews();
    }

    //----------------------------------------------------------------------------------------------

    private void uploadFile(String filePath, String fileName, boolean isDeleteButtonRequired,
                            MasterDataListener<ServerResponse> fileUploadListener) {
        Log.i(getClass().getName() , "----------uploadFile()");

        FileUploadTask fileUploadTask = new FileUploadTask(context, rootView, inflater,
                fileName, isDeleteButtonRequired, fileUploadListener);
        fileUploadTask.execute(filePath);
    }

    private void setDeleteDocButtonClick(final LinearLayout llDocListing, boolean isShowDelete,
                                         final RelativeLayout rlDocList, final String fileName) {
        Log.i(getClass().getName() , "----------setDeleteDocButtonClick()");

        int visibility = isShowDelete ? View.VISIBLE : View.GONE;
        LinearLayout llDelete = (LinearLayout)rlDocList.findViewById(R.id.delete_button_section);

        Button deleteButton = (Button) llDelete.findViewById(R.id.imageButton);
        deleteButton.setText(R.string.btn_delete);
        deleteButton.setVisibility(visibility);
        deleteButton.setCompoundDrawablesWithIntrinsicBounds(
                BabyBonusConstants.BUTTON_DELETE, 0, 0, 0);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageBox.show(context,
                        StringHelper.getStringByResourceId(context, R.string.error_common_do_you_want_to_delete),
                        false, true, R.string.btn_ok, true, R.string.btn_cancel,
                        new MessageBoxButtonClickListener() {
                            @Override
                            public void onClickPositiveButton(DialogInterface dialog, int id) {
                                llDocListing.removeView(rlDocList);
                                map.remove(fileName);
                                dialog.dismiss();
                            }

                            @Override
                            public void onClickNegativeButton(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
            }
        });
    }
}
