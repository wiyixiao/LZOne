package gdut.bsx.share2;

//import android.support.annotation.StringDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ShareContentType
 * Support Share Content Types.
 *
 * @author baishixian
 * @date 2018/3/29 11:41
 */

@StringDef({ShareContentType.TEXT, ShareContentType.IMAGE,
        ShareContentType.AUDIO, ShareContentType.VIDEO, ShareContentType.FILE})
@Retention(RetentionPolicy.SOURCE)
public @interface ShareContentType {
    /**
     * Share Text
     */
    String TEXT = "text/plain";

    /**
     * Share Image
     */
    String IMAGE = "image/*";

    /**
     * Share Audio
     */
    String AUDIO = "audio/*";

    /**
     * Share Video
     */
    String VIDEO = "video/*";

    /**
     * Share File
     */
    String FILE = "*/*";
}
