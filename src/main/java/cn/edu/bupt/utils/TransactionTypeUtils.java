package cn.edu.bupt.utils;

import cn.edu.bupt.model.*;

public final class TransactionTypeUtils {

    private static TransactionManager TM = TransactionManager.getInstance();

    public static void setIOcome(Transaction ta, Tag tag) {

        // System.out.println(tag.getName());

        // tag只能输入收入支出未知三种
        if(tag != Tag.EXPENSE && tag != Tag.INCOME && tag != Tag.UNKNOWN) {
            return;
        }

        if(ta.getTags().contains(Tag.EXPENSE)&&tag==Tag.EXPENSE){
            return;
        }
        else if (ta.getTags().contains(Tag.INCOME)&&tag==Tag.INCOME){
            return;
        }
        else if (ta.getTags().contains(Tag.UNKNOWN)&&tag==Tag.UNKNOWN){
            return;
        }

        // 删去多余的收支tag
        ta.removeTag(Tag.EXPENSE);
        ta.removeTag(Tag.INCOME);
        ta.removeTag(Tag.UNKNOWN);

        // 向交易添加收支
        ta.addTag(tag);

        if(tag == Tag.EXPENSE) {
            setCategory(ta, Tag.EXPENSE_OTHERS);
        }
        else if (tag == Tag.INCOME)  {
            setCategory(ta, Tag.INCOME_OTHERS);
        }
    }

    static public void setCategory(Transaction ta, Tag tag) {

        // tag必须在默认分类当中
        if(TM.incomeTags.contains(tag) && ta.getTags().contains(Tag.INCOME)) {
            for(Tag t:TM.incomeTags) {
                ta.removeTag(t);
            }
            for(Tag t:TM.expenseTags) {
                ta.removeTag(t);
            }
            ta.addTag(tag);
        }
        else if (TM.expenseTags.contains(tag) && ta.getTags().contains(Tag.EXPENSE)) {
            for(Tag t:TM.incomeTags) {
                ta.removeTag(t);
            }
            for(Tag t:TM.expenseTags) {
                ta.removeTag(t);
            }
            ta.addTag(tag);
        }
        else {
            return;
        }
    }
}