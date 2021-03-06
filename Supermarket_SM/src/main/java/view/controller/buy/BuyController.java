package view.controller.buy;

import com.beanvo.*;
import com.jfoenix.controls.*;

import com.service.impl.InTableServiceImpl;
import com.spring.BaseHolder;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import view.util.Manage;
import view.util.StageManage;
import view.util.ViewAssistImpl;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.spring.BeanManage.buyService;
import static com.spring.BeanManage.inTableService;

/**
 * @ClassName
 * @Description
 * @Author LGH
 * @Date 2020/7/12 17:29
 * @Version 1.0
 **/
public class BuyController extends ViewAssistImpl implements Initializable {


    /**BuyTable**/
    @FXML
    public JFXTextField buySearchField;
    @FXML
    public JFXTreeTableView<BuyVo> buyTreeTableView;
    @FXML
    public JFXTreeTableColumn<BuyVo,Boolean> buy_selected;
    @FXML
    public JFXTreeTableColumn<BuyVo,Integer>buy_stockid,buy_supid,buy_gid,buy_perid;
    @FXML
    public JFXTreeTableColumn<BuyVo,String>buy_gname,buy_model,buy_number,buy_pname,buy_set;
    @FXML
    public JFXTreeTableColumn<BuyVo, String>buy_cometime,buy_mtime,buy_update,buy_expiration;
    @FXML
    public JFXTreeTableColumn<BuyVo, Double>buy_price,buy_prices;
    /**  Sync from Mysql date**/
    private static ObservableList<BuyVo> BuyDate= FXCollections.observableArrayList();


    /**inTable**/
    @FXML
    public JFXTextField inTableSearchField;
    @FXML
    public JFXTreeTableView<IntableVo> inTableTreeTableView;
    @FXML
    public JFXTreeTableColumn<IntableVo,Boolean> in_selected;
    @FXML
    public JFXTreeTableColumn<IntableVo,Integer>in_inid,in_gid,in_inperid,in_buy;
    @FXML
    public JFXTreeTableColumn<IntableVo,String >in_quantity,in_cometime,in_pname,in_gname,in_stockroom;
    @FXML
    public HBox P_buy_Error,P_inTable_Error;
    @FXML
    public Label L_buy_Error,L_inTable_Error;

    // editable table view
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Manage.setBuyController(this);
        P_buy_Error.setVisible(false);
        P_inTable_Error.setVisible(false);

        buyService.setBuyJFXTreeTableColumn(buy_selected,buy_stockid ,buy_supid, buy_perid, buy_gid ,
                                                buy_gname ,buy_model, buy_number, buy_pname, buy_cometime ,
                                                buy_mtime ,buy_price, buy_prices ,buy_set,buy_update,buy_expiration);

        buyService.show_BuyJFXTreeTableColumn();
        buySearchField.textProperty().addListener(BuysetupSearchField(buyTreeTableView));

        inTableService = BaseHolder.getApplicationContext().getBean("inTableService", InTableServiceImpl.class);
        inTableService.setInTableJFXTreeTableColumn(in_selected,in_inid,in_gid,in_inperid,in_buy,
                                                     in_quantity,in_cometime,in_stockroom,in_cometime,
                                                    in_pname,in_gname);

        inTableService.showInTableJFXTreeTableColumn();
        inTableSearchField.textProperty().addListener(inTablesetupSearchField(inTableTreeTableView));

    }



    private Stage buyUpdateStage;
    @FXML
    public void buyUpdate() throws IOException {
        BuyControllerInsertUpdate.setTemp(0);
        StageManage.getBuyAddStage().show();
        //自动递增编号
        Manage.getBuyControllerInsertUpdate().inid.setText(buyService.getNewIDByLastID().toString());
        Manage.getBuyControllerInsertUpdate().L_model.setText("增加");
    }



    @FXML
    public void B_inTableSelected() {
        if(inTableService.Selected()==0){
            super.setMessageType(P_inTable_Error,L_inTable_Error,"error");
//            设置错误的提示
            L_inTable_Error.setText("请至少选中一个需要删除的的对象");
        } else {
            super.setMessageType(P_inTable_Error,L_inTable_Error,"message");
            L_inTable_Error.setText("已成功删除选中的对象");
            buyService.show_BuyJFXTreeTableColumn();
            inTableService.showInTableJFXTreeTableColumn();
            Manage.getController().buyModelRStart();
        }
    }
    @FXML
    public void B_Buyselected() {
        if( buyService.buySelected()==0){
            super.setMessageType(P_buy_Error,L_buy_Error,"error");
//            设置错误的提示
            L_buy_Error.setText("请至少选中一个需要删除的的对象");
        }else {
            super.setMessageType(P_buy_Error, L_buy_Error, "message");
            L_buy_Error.setText("已成功删除选中的对象");
            buyService.show_BuyJFXTreeTableColumn();
            Manage.getController().buyModelRStart();
        }
    }




    @FXML//刷新按钮
    public void B_inTableRefresh(){
        System.out.println("入库管理新按钮按下");
        inTableTreeTableView.setRoot(null);
        inTableService.showInTableJFXTreeTableColumn();
    }
    @FXML//刷新按钮
    public void B_buyRefresh(){
        System.out.println("采购管理刷新按钮按下");
        buyTreeTableView.setRoot(null);
        buyService.show_BuyJFXTreeTableColumn();

    }



    @FXML//user buy_Error Windows Colse
    public void buy_Error_hide(){
        P_buy_Error.setVisible(false);
    }
    @FXML//user inTable_Error Windows Close
    public void inTable_Error_hide(){
        P_inTable_Error.setVisible(false);
    }


    //    模糊查询信息
    private ChangeListener<String> BuysetupSearchField(final JFXTreeTableView<BuyVo> tableView) {
        return (o, oldVal, newVal) ->
                tableView.setPredicate(buyVoTreeItem -> {
                    final BuyVo buyVo = buyVoTreeItem.getValue();
                    return
                            buyVo.getGname().contains(newVal)
                                    ||buyVo.getModel().contains(newVal)
                                    ||buyVo.getPname().contains(newVal)
                                    ||Integer.toString(buyVo.getBuyid()).contains(newVal)
                                    ||Integer.toString(buyVo.getGid()).contains(newVal)
                                    ||buyVo.getComeTime().toString().contains(newVal)
                                    ||buyVo.getQuantity().contains(newVal)
                                    ||Double.toString(buyVo.getPrice()).contains(newVal)
                                    ||Double.toString(buyVo.getPrices()).contains(newVal)
                                    ||buyVo.getProduced().toString().contains(newVal)
                                    ||Integer.toString(buyVo.getSupplier()).contains(newVal)
                                    ||buyVo.getSupname().contains(newVal);

                });
    }
    //    模糊查询信息
    private ChangeListener<String> inTablesetupSearchField(final JFXTreeTableView<IntableVo> tableView) {
        return (o, oldVal, newVal) ->
                tableView.setPredicate(intableVoTreeItem -> {
                    final IntableVo intableVo = intableVoTreeItem.getValue();


                    return
                            intableVo.getWname().contains(newVal)
                                    ||intableVo.getQuantity().contains(newVal)
                                    ||intableVo.getPname().contains(newVal)
                                    ||intableVo.getGname().contains(newVal)
                                    ||Integer.toString(intableVo.getInid()).contains(newVal)
                                    ||Integer.toString(intableVo.getBuyid()).contains(newVal)
                                    ||Integer.toString(intableVo.getGid()).contains(newVal)
                                    ||intableVo.getCometime().toString().contains(newVal)
                                    ||Integer.toString(intableVo.getInPerid()).contains(newVal);



                });
    }

    }
    


