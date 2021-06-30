[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Fragmentation-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5937)
[![Build Status](https://travis-ci.org/YoKeyword/Fragmentation.svg?branch=master)](https://travis-ci.org/YoKeyword/Fragmentation)
[![Download](https://api.bintray.com/packages/yokeyword/maven/Fragmentation/images/download.svg) ](https://bintray.com/yokeyword/maven/Fragmentation/_latestVersion)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)

#butterknife 使用

1//绑定初始化ButterKnife
       activity 中使用 ButterKnife.bind(this);
       fragment中使用
       public class ButterknifeFragment extends Fragment{
           private Unbinder unbinder;
           @Override
           public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
               View view = inflater.inflate(R.layout.fragment, container, false);
               //返回一个Unbinder值（进行解绑），注意这里的this不能使用getActivity()
               unbinder = ButterKnife.bind(this, view);
               return view;
           }

           /**
            * onDestroyView中进行解绑操作
            */
           @Override
           public void onDestroyView() {
               super.onDestroyView();
               unbinder.unbind();
           }
       }
2 绑定view
       @BindView( R2.id.button)
       public Button button;

         @OnClick({R.id.ll_product_name, R.id.ll_product_lilv, R.id.ll_product_qixian, R.id.ll_product_repayment_methods})
           public void onViewClicked(View view) {
               switch (view.getId()) {
                   case R.id.ll_product_name:
                       System.out.print("我是点击事件1");
                       break;
                   case R.id.ll_product_lilv:
                       System.out.print("我是点击事件2");
                       break;
                   case R.id.ll_product_qixian:
                       System.out.print("我是点击事件3");

                       break;
                   case R.id.ll_product_repayment_methods:
                       System.out.print("我是点击事件4");
                       break;
               }
           }