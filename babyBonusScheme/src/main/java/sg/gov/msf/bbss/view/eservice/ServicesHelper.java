package sg.gov.msf.bbss.view.eservice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;

import sg.gov.msf.bbss.BbssApplication;
import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.FragmentContainerActivityHelper;
import sg.gov.msf.bbss.logic.masterdata.MasterDataCache;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.ServerConnectionHelper;
import sg.gov.msf.bbss.logic.server.task.GetLocalAddressTask;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;

/**
 * Created by bandaray
 */
public class ServicesHelper {

    private Context context;

    public ServicesHelper(Context context) {
        this.context = context;
    }

    //--- ALERT DIALOGS ----------------------------------------------------------------------------

    public void createOkToCancelMessageBox() {
        FragmentContainerActivityHelper helper = new FragmentContainerActivityHelper(context);
        helper.createOkToCancelMessageBox(new MessageBoxButtonClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                context.startActivity(new Intent(context, ServicesHomeActivity.class));
            }

            @Override
            public void onClickNegativeButton(DialogInterface dialog, int id) {

            }
        });
    }

    public void createNoChildToDisplayMessageBox() {
        MessageBox.show(context,
                StringHelper.getStringByResourceId(context, R.string.error_services_no_child_data),
                false, true, R.string.btn_ok, false, 0, null);
    }

    public void createDeviceOfflineMessageBox() {
        ServerConnectionHelper.createDeviceOfflineMessageBox(context,
                new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        context.startActivity(new Intent(context, ServicesHomeActivity.class));
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {

                    }
                });
    }

    public void createNoChildSelectedMessageBox() {
        MessageBox.show(context,
                StringHelper.getStringByResourceId(context, R.string.error_services_select_child),
                false, true, R.string.btn_ok, false, 0, new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        Log.i(getClass().getName(), "----------onClickPositiveButton()");

                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {
                        Log.i(getClass().getName(), "----------onClickNegativeButtons()");

                        dialog.dismiss();
                    }
                });
    }

    public void createDeclarationRequiredMessageBox() {
        MessageBox.show(context,
                StringHelper.getStringByResourceId(context,
                        R.string.desc_services_common_declaration_required),
                false, true, R.string.btn_ok, false, 0, new MessageBoxButtonClickListener() {
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

    public void createClientValidationIssuesMessageBox(MessageBoxButtonClickListener listener) {
        MessageBox.show(context, StringHelper.getStringByResourceId(
                        context, R.string.error_common_form_not_properly_completed),
                false, true, R.string.btn_ok, false, 0, listener);
    }

    public void createServiceResponseMessageBox(final BbssApplication app, String message,
                                                final String appId,final ChildListType serviceType) {
        MessageBox.show(context, message, false, true, R.string.btn_ok, false, 0,
                new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        switch (serviceType) {
                            case CHANGE_NAH:
                                app.setServiceChangeNah(null);
                                break;
                            case CHANGE_NAN:
                                app.setServiceChangeNan(null);
                                break;
                            case CHANGE_CDAT:
                                app.setServiceChangeCdat(null);
                                break;
                            case CHANGE_CDAB:
                                app.setServiceChangeCdab(null);
                                break;
                            case CDA_TO_PSEA:
                                app.getServiceTransferToPsea();
                                break;
                            case CHANGE_BO:
                                app.setServiceChangeBo(null);
                                break;
                            case TERMS_AND_COND:
                                app.setServiceCdabTc(null);
                                break;
                            case OPEN_CDA:
                                app.setServiceOpenCda(null);
                                break;
                        }

                        Intent intent = new Intent(context, ServiceAcknowledgementActivity.class);
                        intent.putExtra(BabyBonusConstants.SERVICES_APP_ID, appId);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
    }

    //--- MASTER DATA ------------------------------------------------------------------------------

    public void displayBanks(final ArrayAdapter<Bank> adapter){
        FragmentContainerActivityHelper helper = new FragmentContainerActivityHelper(context);
        helper.displayBanks(adapter);
    }

    public void displayBankMa(final ArrayAdapter<Bank> adapter){
        FragmentContainerActivityHelper helper = new FragmentContainerActivityHelper(context);
        helper.displayBankMa(adapter);
    }

    public void displayChangeReasons(final ArrayAdapter<GenericDataItem> adapter){
        MasterDataCache masterDataCache = new MasterDataCache(context);
        masterDataCache.getGenericDataItems(MasterDataType.CDA_BANK_CHANGE_REASON, new MasterDataListener<GenericDataItem[]>() {
            @Override
            public void onMasterData(GenericDataItem[] masterDataItems) {
                adapter.addAll(masterDataItems);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void displayGenericData(MasterDataType type, ArrayAdapter<GenericDataItem> adapter){
        FragmentContainerActivityHelper helper = new FragmentContainerActivityHelper(context);
        helper.displayGenericData(type, adapter);
    }

    //--- HELPERS ----------------------------------------------------------------------------------

    public void displayAddressByPostalCode(final ModelViewSynchronizer<Address>
                                                   addressModelViewSynchronizer){
        final Address localAddress = addressModelViewSynchronizer.getDataObject();

        if(localAddress == null || localAddress.getPostalCode()==0){
            return;
        }

        GetLocalAddressTask addressTask =
                new GetLocalAddressTask(context, new MasterDataListener<Address>() {
                    @Override
                    public void onMasterData(Address address) {
                        if(address == null){
                            address = new Address();
                            address.setPostalCode(localAddress.getPostalCode());

                            MessageBox.show(context,
                                    StringHelper.getStringByResourceId(context, R.string.error_common_invalid_postalcode),
                                    true, true, R.string.btn_ok, false, 0, null);
                        }

                        addressModelViewSynchronizer.displayDataObject(address);
                    }
                });

        addressTask.execute(localAddress.getPostalCode());
    }

}
