package mobi.hubtech.goacg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.fima.cardsui.objects.Card;

public class EmptyCard extends Card {

    @Override
    public View getCardContent(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.program_item_empty, null);
        return view;
    }
    
    @Override
    protected int getCardLayout() {
        return R.layout.program_card;
    }
    
    @Override
    protected int getFirstCardLayout() {
        return R.layout.program_card;
    }
    
    @Override
    protected int getLastCardLayout() {
        return R.layout.program_card;
    }
}
