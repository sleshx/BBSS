package sg.gov.msf.bbss.logic.adapter.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.type.ServiceAppType;

/**
 * Created by bandaray
 */
public class CustomGridListViewItem {

    ServiceAppType serviceType;
	String title;
	int imageId;
	Bitmap imageBmp;
    int badgeCount;

	public CustomGridListViewItem(Context context, int imageId, int title) {
		super();
		this.title = StringHelper.getStringByResourceId(context, title);
		this.imageId = imageId;
		imageBmp = BitmapFactory.decodeResource(context.getResources(), imageId);
	}

    public CustomGridListViewItem(Context context, ServiceAppType serviceType) {
        super();
        this.serviceType = serviceType;
        this.title = serviceType.getStatusTitle(context);
        this.imageBmp = serviceType.getImageBitmap(context);
    }

    public ServiceAppType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceAppType serviceType) {
        this.serviceType = serviceType;
    }

    public Bitmap getImage() {
		return imageBmp;
	}
	public void setImage(Bitmap image) {
		this.imageBmp = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }
}
