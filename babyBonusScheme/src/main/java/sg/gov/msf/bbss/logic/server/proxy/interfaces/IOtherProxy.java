package sg.gov.msf.bbss.logic.server.proxy.interfaces;

import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.model.entity.ServiceStatus;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.childdata.ChildStatement;
import sg.gov.msf.bbss.model.wizardbase.SiblingCheck;
import sg.gov.msf.bbss.model.wizardbase.UpdateProfile;

/**
 * Created by bandaray on 12/5/2015.
 */
public interface IOtherProxy {
    ChildItem[] getChildItemList();

    ChildStatement getChildStatement(String childId);

    UpdateProfile getUserProfile();

    ServerResponse updateUserProfile(UpdateProfile updateProfile);

    ServerResponse checkSiblingHood(SiblingCheck siblingCheck);

    ServiceStatus[] getServiceAppStatuses();
}
