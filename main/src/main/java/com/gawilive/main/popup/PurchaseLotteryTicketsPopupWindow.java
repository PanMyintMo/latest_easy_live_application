package com.gawilive.main.popup;

import android.content.Context;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.hjq.shape.view.ShapeTextView;
import com.lxj.xpopup.core.BottomPopupView;
import com.gawilive.common.utils.BigDecimalUtils;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.main.R;

// 购买奖券
public class PurchaseLotteryTicketsPopupWindow extends BottomPopupView {
    private ImageView mImgClose;
    private ImageView mImgJian;
    private ImageView mImgJia;
    private TextView mTvMyJF;
    private ShapeTextView mTvConfirm;

    // 单价
    private String unitCoin;

    private String myCoin;

    private TextView tvNumber;

    private TextView tvJf, tvJFDesc;

    public PurchaseLotteryTicketsPopupWindow(@NonNull Context context, String unitCoin, String myCoin, OnConfirmClickListener onConfirmClickListener) {
        super(context);
        this.unitCoin = unitCoin;
        this.myCoin = myCoin;
        this.onConfirmClickListener = onConfirmClickListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_purchase_lottery_tickets;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initView();
        mImgJia.setOnClickListener(v -> {
            String number = tvNumber.getText().toString().trim();
            String str = BigDecimalUtils.add(number, "1", 0);
            if (Integer.parseInt(str) > 5) {
                ToastUtil.show("一次最多购买5张奖券");
                return;
            }
            tvNumber.setText(str);
            tvJf.setText(BigDecimalUtils.mul(unitCoin, str, 0));
        });

        mImgJian.setOnClickListener(v -> {
            String number = tvNumber.getText().toString().trim();
            String str = BigDecimalUtils.sub(number, "1", 0);
            if ("0".equals(number)) {
                return;
            }
            tvJf.setText(BigDecimalUtils.mul(unitCoin, str, 0));
            tvNumber.setText(str);
        });
        mTvMyJF.setText("剩余" + myCoin + "积分");
    }

    private void initView() {
        mImgClose = findViewById(R.id.imgClose);
        mImgJian = findViewById(R.id.imgJian);
        mImgJia = findViewById(R.id.imgJia);
        mTvMyJF = findViewById(R.id.tvMyJF);
        mTvConfirm = findViewById(R.id.tv_confirm);
        tvNumber = findViewById(R.id.tvNumber);
        tvJf = findViewById(R.id.tvJf);
        tvJFDesc = findViewById(R.id.tvJFDesc);
        tvJFDesc.setText("1张奖券/" + unitCoin + "积分");
        mTvConfirm.setOnClickListener(v -> {
            String number = tvNumber.getText().toString().trim();
            if ("0".equals(number)) {
                ToastUtil.show("至少抽奖一次");
                return;
            }
            onConfirmClickListener.onConfirm(number);
            this.dialog.dismiss();
        });
        mImgClose.setOnClickListener(v -> {
            this.dialog.dismiss();
        });
    }

    private OnConfirmClickListener onConfirmClickListener;

    public interface OnConfirmClickListener {
        void onConfirm(String number);
    }
}
