package com.kustomer.kustomersdk.Helpers;

import android.widget.TextView;

import org.commonmark.parser.Parser;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.spans.SpannableTheme;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSText {

    //region Public Methods
    public static void setMarkDownText(TextView textView, String text){

        SpannableTheme theme = SpannableTheme.builderWithDefaults(textView.getContext())
                .linkColor(textView.getTextColors().getDefaultColor())
                .build();

        SpannableConfiguration spannableConfiguration = SpannableConfiguration.builder(textView.getContext())
                .theme(theme)
                .build();

        text = text.replaceAll("\n","\n\n");
        Markwon.setMarkdown(textView,spannableConfiguration,text);
    }
    //endregion

}
