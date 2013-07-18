package mobi.hubtech.goacg;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuDialogFragment extends DialogFragment {
    
    private ViewGroup mLayoutStaff;
    private ListView mListViewMenu;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_dialog, container, false);
        
        mLayoutStaff = (ViewGroup) view.findViewById(R.id.layout_staff);
        mListViewMenu = (ListView) view.findViewById(R.id.list_menu);
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] menuItems = getResources().getStringArray(R.array.menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
                android.R.layout.simple_list_item_1, android.R.id.text1, menuItems);
        mListViewMenu.setAdapter(adapter);
        mListViewMenu.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                switch (position) {
                case 0: onMenuShareClick(); break;
                case 1: onMenuAboutClick(); break;
                }
            }
        });
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.Transparent_Dialog);
        return dialog;
    }
    
    private void onMenuShareClick() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "追新番App分享");
        intent.putExtra(Intent.EXTRA_TEXT, "和邪社推的追新番，订阅动画还挺方便，你也玩玩看 http://hexieshe.com/goacg");
        intent = Intent.createChooser(intent, getString(R.string.app_name));
        startActivity(intent);
        dismiss();
    }
    
    private void onMenuAboutClick() {
        switchView(mListViewMenu, mLayoutStaff);
    }
    
    private void switchView(final View foreView, final View backView) {
        backView.setVisibility(View.INVISIBLE);
        backView.animate().setDuration(0).scaleX(0);
        foreView.animate().setDuration(250).setInterpolator(new AccelerateInterpolator(1.5f)).scaleX(0).setListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                backView.animate().setDuration(250).setInterpolator(new DecelerateInterpolator(1.5f)).scaleX(1);
                backView.setVisibility(View.VISIBLE);
                foreView.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }
}
