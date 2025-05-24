package cn.edu.bupt.utils;

import cn.edu.bupt.model.*;

/**
 * Utility class for managing transaction types and categories.
 * This class provides methods to set and update the transaction type (income/expense)
 * and category tags for Transaction objects.
 * 
 * The class ensures that transactions are properly categorized and maintains
 * consistency of tags by removing conflicting categories when a new one is assigned.
 * 
 * @author BUPT Account Book Team
 * @version 1.0
 */
public final class TransactionTypeUtils {

    private static TransactionManager TM = TransactionManager.getInstance();    
    /**
     * Sets the income/expense type for a transaction.
     * This method assigns one of the primary classification tags (EXPENSE, INCOME, or UNKNOWN)
     * to a transaction and ensures that only one such tag is present.
     * 
     * If the transaction is categorized as an expense or income, it will also be assigned
     * a default subcategory (EXPENSE_OTHERS or INCOME_OTHERS).
     * 
     * @param ta The transaction to update
     * @param tag The primary classification tag (must be EXPENSE, INCOME, or UNKNOWN)
     */
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
    
    /**
     * Sets the specific category for a transaction.
     * This method assigns a subcategory tag to a transaction based on its primary type
     * (income or expense) and ensures that only one subcategory is present.
     * 
     * If the transaction's primary type doesn't match the category type (e.g., trying to
     * assign an expense category to an income transaction), the method will do nothing.
     * 
     * @param ta The transaction to update
     * @param tag The subcategory tag to assign
     */
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