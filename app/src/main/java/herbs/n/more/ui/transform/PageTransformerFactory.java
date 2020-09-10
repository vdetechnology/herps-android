package herbs.n.more.ui.transform;

import androidx.viewpager2.widget.ViewPager2;

import com.zhpan.bannerview.transform.ScaleInTransformer;
import static herbs.n.more.ui.transform.TransformerStyle.ACCORDION;
import static herbs.n.more.ui.transform.TransformerStyle.DEPTH;
import static herbs.n.more.ui.transform.TransformerStyle.ROTATE;
import static herbs.n.more.ui.transform.TransformerStyle.SCALE_IN;
import static herbs.n.more.ui.transform.TransformerStyle.STACK;

public class PageTransformerFactory {

    public static ViewPager2.PageTransformer createPageTransformer(int transformerStyle) {
        ViewPager2.PageTransformer transformer = null;
        switch (transformerStyle) {
            case DEPTH:
                transformer = new DepthPageTransformer();
                break;
            case ROTATE:
                transformer = new RotateUpTransformer();
                break;
            case STACK:
                transformer = new StackTransformer();
                break;
            case ACCORDION:
                transformer = new AccordionTransformer();
                break;
            case SCALE_IN:
                transformer = new ScaleInTransformer(ScaleInTransformer.DEFAULT_MIN_SCALE);
                break;
        }
        return transformer;
    }
}
