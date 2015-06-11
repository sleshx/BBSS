package sg.gov.msf.bbss.logic.server.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.connect.HttpJsonCaller;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.logic.SupportingDocumentsHelper;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.server.proxy.dev.AppUrls;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;

/**
 * Created by bandaray
 */
public class FileUploadTask  extends AsyncTask<String, Void, ServerResponse> {
    private View rootView;
    private Context context;
    private LayoutInflater inflater;

    private String fileName;
    private boolean isDeleteButtonRequired;

    private final MasterDataListener<ServerResponse> fileUploadListener;
    private final ProgressDialog dialog;

    public FileUploadTask(Context context, View rootView, LayoutInflater inflater,
                          String fileName, boolean isDeleteButtonRequired,
                          MasterDataListener<ServerResponse> fileUploadListener) {
        this.rootView = rootView;
        this.context = context;
        this.inflater = inflater;
        this.fileName = fileName;
        this.isDeleteButtonRequired = isDeleteButtonRequired;
        this.fileUploadListener = fileUploadListener;
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Uploading file");
        dialog.show();
    }

    @Override
    protected ServerResponse doInBackground(String... params) {
        ServerResponse updateResponse = new ServerResponse();
        File file = new File(params[0]);
        long fileSize = file.length();

        if (fileSize > AppConstants.APP_MAX_UPLOAD_FILE_SIZE) {
            updateResponse.setMessage(String.format("File size cannot exceed %s",
                    AppConstants.APP_MAX_UPLOAD_FILE_SIZE_NAME));
            updateResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
        } else {
            byte[] bytes = new byte[(int) fileSize];
            FileInputStream fileStream = null;

            try {
                fileStream = new FileInputStream(file);
                fileStream.read(bytes);
                String encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);

                JSONObject jsonFinal = new JSONObject();
                JSONObject jsonRoot = new JSONObject();

                jsonRoot.put("fileName", file.getName());
                jsonRoot.put("fileBinary", encodedFile);
                jsonFinal.put("supFiles", jsonRoot);

                HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
                String jsonString = httpJsonCaller.post(AppUrls.UPLOAD_FILE_URL,
                        jsonFinal.toString());
//                JSONObject jsonResponse = new JSONObject(jsonString).getJSONObject(
//                        SerializedNames.SEC_RESPONSE_STATUS);

                updateResponse.setFile(file);
                updateResponse.setResponseType(ServerResponseType.SUCCESS);
//                updateResponse.setCode(jsonResponse.getString(SerializedNames.SEC_RESPONSE_CODE));
//                updateResponse.setMessage(jsonResponse.getString(SerializedNames.SEC_RESPONSE_MESSAGE));

            } catch (FileNotFoundException e) {
                updateResponse.setMessage("Uploading file cannot be found in the device.");
                updateResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            } catch (IOException e) {
                updateResponse.setMessage("Unable to read contents of the file");
                updateResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            } catch (JSONException e) {
                updateResponse.setMessage("Unable to upload file.");
                updateResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            } catch (Exception e) {
                updateResponse.setMessage("Unable to contact server.");
                updateResponse.setResponseType(ServerResponseType.SERVICE_ERROR);
            } finally {
                try {
                    if (fileStream != null) {
                        fileStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return updateResponse;
    }

    @Override
    protected void onPostExecute(ServerResponse response) {
        if (response.getResponseType() == ServerResponseType.SUCCESS) {
            fileUploadListener.onMasterData(response);

            SupportingDocumentsHelper docHelper = new SupportingDocumentsHelper(rootView,
                    context, inflater);
            docHelper.addDocumentItemView(fileName, isDeleteButtonRequired);
        } else {
            MessageBox.show(context, response.getMessage(), false, true, R.string.btn_ok, false, 0,
                    new MessageBoxButtonClickListener() {
                        @Override
                        public void onClickPositiveButton(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onClickNegativeButton(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
