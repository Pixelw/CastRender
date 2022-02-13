package tech.pixelw.castrender.utils;

import android.text.SpannableStringBuilder;

public class SpanStringBuilder extends SpannableStringBuilder {
    @Override
    public SpanStringBuilder append(CharSequence text, Object what, int flags) {
        int start = length();
        append(text);
        setSpan(what, start, length(), flags);
        return this;
    }
    public SpanStringBuilder append(CharSequence text){
         super.append(text);
         return this;
    }
}
