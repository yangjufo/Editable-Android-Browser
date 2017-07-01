package com.example.browser.fragment;


import android.content.Context;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.browser.constance.fragConst;

/**
 * Created by Administrator on 2016/11/28.
 */
public class fragAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private FragmentManager fm;
    public fragAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.context = context;
        this.fm=fm;
    }

    @Override
    public Fragment getItem(int position) {
        return fragConst.fraglist.get(position);
    }

    @Override
    public int getCount() {
        // Logger.v("fragConst.fraglist.size() "+fragConst.fraglist.size());
        return fragConst.fraglist.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        return super.instantiateItem(container, position);
    }


    @Override
    public int getItemPosition(Object object) {
//        try {
//            Method method =  object.getClass().getMethod(("getFragTag"),new Class[0]    );
//            String tag= (String) method.invoke(object,new Object[0]);
//            if( isDel ){
//                if( tag.equals(""+id  )  ){
//                    Logger.d("删除项tag=   "+tag+ "   id=  " +  id );
//                    return POSITION_NONE;
//                }else {
//                    return POSITION_UNCHANGED;
//                }
//            }else {
//                return POSITION_UNCHANGED;
//            }
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            Logger.d("getItemPosition  InvocationTargetException"  );
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            Logger.d("getItemPosition  IllegalAccessException"  );
//            e.printStackTrace();
//        }

        //需要实现一种机制避免更新所有的fragment
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

    }
}
