/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Account;
import Model.Cart;
import Model.Customer;
import Model.Items;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class AccountDAO extends DBContext {

    public Account check(String username, String password) {
        String sql = "SELECT [Username]\n"
                + "      ,[Password]\n"
                + "      ,[role]\n"
                + "  FROM [dbo].[Account]\n"
                + "  where Username = ? and Password = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Account a = new Account(rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getInt("role"));
                return a;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    public void insertAccount(String username, String password, String fullname, int phone, String email, String address) {
        String sql = """
                 DECLARE @customer_id int;
                 INSERT INTO [dbo].[Customer]
                            ([fullname]
                            ,[phone]
                            ,[email]
                            ,[address])
                      VALUES
                            (?,?,?,?);
                 SET @customer_id = SCOPE_IDENTITY();
                 
                 INSERT INTO [dbo].[Account]
                            ([username]
                            ,[password]
                            ,[role]
                            ,[customer_id])
                      VALUES
                            (?,?,?,@customer_id)
                 """;
        int role = 2;
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, fullname);
            st.setInt(2, phone);
            st.setString(3, email);
            st.setString(4, address);
            st.setString(5, username);
            st.setString(6, password);
            st.setInt(7, role);
            st.executeUpdate();
        } catch (SQLException e) {

        }

    }

    public void insertCustomer(String fullname, int phone, String email, String address, int userID) {
        String sql = "INSERT INTO [dbo].[Customer]\n"
                + "           ([user_id]\n"
                + "           ,[fullname]\n"
                + "           ,[phone]\n"
                + "           ,[email]\n"
                + "           ,[address])\n"
                + "     VALUES\n"
                + "           (?,?,?,?,?)";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, userID);
            st.setString(2, fullname);
            st.setInt(3, phone);
            st.setString(4, email);
            st.setString(5, address);

            st.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();
        try {
            String sql = "SELECT [account_id]\n"
                    + "      ,[username]\n"
                    + "      ,[password]\n"
                    + "      ,[role]\n"
                    + "      ,[customer_id]\n"
                    + "  FROM [dbo].[Account]";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Account a = new Account();
                a.setUsername(rs.getString("username"));
                a.setPassword(rs.getString("password"));
                a.setRole(rs.getInt("role"));
                Customer c = new Customer();
                c.setCustomer_id(rs.getInt("customer_id"));
                accounts.add(a);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return accounts;
    }

    public boolean isExist(List<Account> account, String name) {
        boolean isExist = false;
        account = getAccounts();
        for (Account a : account) {
            if (a.getUsername().equalsIgnoreCase(name)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    public void addOrder(Customer c, Cart cart) {
        LocalDate curDate = LocalDate.now();
        String date = curDate.toString();
        try {
            String sql = "INSERT INTO [dbo].[Order]\n"
                    + "           ([order_date]\n"
                    + "           ,[customer_id]\n"
                    + "           ,[total_money])\n"
                    + "     VALUES\n"
                    + "           (?,?,?)";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, date);
            st.setInt(2, c.getCustomer_id());
            st.setFloat(3, cart.getTotalMoney());
            st.executeUpdate();
            //lay id cua order vua add
            String sql1 = "select top 1 order_id from [Order] Order by order_id desc";
            PreparedStatement st1 = connection.prepareStatement(sql1);
            ResultSet rs = st1.executeQuery();
            //add bang orderdetail
            if (rs.next()) {
                int oid = rs.getInt("order_id");
                for (Items i : cart.getItems()) {
                    String sql2 = "INSERT INTO [dbo].[OrderDetail]\n"
                            + "           ([order_id]\n"
                            + "           ,[racket_id]\n"
                            + "           ,[quantity]\n"
                            + "           ,[price])\n"
                            + "     VALUES\n"
                            + "           (?,?,?,?) ";
                    PreparedStatement st2 = connection.prepareStatement(sql2);
                    st2.setInt(1, oid);
                    st2.setInt(2, i.getRacketSpecs().getRacket_id());
                    st2.setInt(3, i.getQuantity());
                    st2.setFloat(4, i.getPrice());
                    st2.executeUpdate();
                }
            }
            //cap nhap lai so luong san pham
            String sql3 = "update RacketSpecs set quantity=quantity - ? where racket_id = ?";
            PreparedStatement st3 = connection.prepareStatement(sql3);
            for (Items i : cart.getItems()) {
                st3.setInt(1, i.getQuantity());
                st3.setInt(2, i.getRacketSpecs().getRacket_id());
                st3.executeUpdate();
            }
        } catch (SQLException e) {
        }
    }

    public Customer getCustomerByAccount(String name) {
        String sql = "select * from Customer c\n"
                + "join Account a on c.customer_id = a.customer_id\n"
                + "where a.username = ? ";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomer_id(rs.getInt("customer_id"));
//                Account a = new Account();
//                a.setUsername(rs.getString("username"));
//                a.setPassword(rs.getString("password"));
//                a.setAccount_id(rs.getInt("account_id"));
                c.setFullname(rs.getString("fullname"));
                c.setPhone(rs.getInt("phone"));
                c.setEmail(rs.getString("email"));
                c.setAddress(rs.getString("address"));
                return c;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    public Account getAccountByname(String name) {
        AccountDAO ab = new AccountDAO();
        List<Account> list = ab.getAccounts();
        for (Account a : list) {
            if (a.getUsername().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        AccountDAO ac = new AccountDAO();
        Customer customer = ac.getCustomerByAccount("tuan");
        System.out.println(customer.toString());
        ac.insertAccount("nam", "abc", "nam", 02323, "hahah", "hanoi");
//        System.out.println(c.toString());
    }
}
