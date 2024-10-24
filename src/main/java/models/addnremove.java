//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public ManageBooks () {
//initComponents();
//setBookDetailsToTable();
//}
//
//
//public void setBookDetailsToTable() {
//
//try {
//
//Class.forName("com.mysql.jdbc.Driver");
//
//Connection con DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root","your_password");
//Statement st = con.createStatement();
//ResultSet rs = st.executeQuery("select * from book_details");
//while(rs.next()) {
//String bookId=rs.getString("book_id");
//String bookName rs.getString("book_name");
//String author rs.getString("author");
//int quantity rs.getInt("quantity");
//Object[] obj (bookId, bookName, author, quantity);
//model (DefaultTableModel) tbl_bookDetails.getModel();
//model.addRow(obj); }
//}
//catch (Exception e) {
//e.printStackTrace (); }
//}
//
//public boolean addBook() {
//boolean isAdded false;
//bookId Integer.parseInt(txt_bookId.getText());
//bookName = txt_bookName.getText();
//author txt_authorName.getText();
//quantity Integer.parseInt(txt_quantity.getText());
//
//try {
//Connection con DBConnection.getConnection();
//String sql "insert into book_details values(?,?,?,?)";
//PreparedStatement pst - con.prepareStatement (sql);
//pst.setInt(1, bookId);
//pst.setString(2, bookName);
//pst.setString(3, author);
//pst.setInt(4, quantity);
//int rowCount = pst.executeUpdate();
//if (rowCount > 0) {
//isAdded = true;
//}
//else {
//isAdded = false;
//}
//} catch (Exception e) {
//e.printStackTrace ();
//}
//return isAdded;
//}
//
//
//public void clearTable ()
//{ DefaultTableModel model = (DefaultTableModel) tbl_bookDetails.getModel();
//model.setRowCount(0);
//}
//
//private void rSMaterialButtonCircle2ActionPerformed (java.awt.event.ActionEvent evt) {
//
//if (addBook() == true) {
//JOptionPane.showMessageDialog(this, "Book Added");
//clearTable();
//setBookDetailsToTable();
//}else{
//JOptionPane.showMessageDialog(this, "Book Addition Failed");}
//
//
//public boolean deleteBook() {
//boolean isDeleted false;
//bookId = Integer.parseInt(txt_bookId.getText());
//
//try {
//Connection con = DBConnection.getConnection();
//String sql = "delete from book_details where book_id = ?";
//PreparedStatement pst = con.prepareStatement (sql);
//pst.setInt(1, bookId);
//int rowCount pst.executeUpdate();
//if (rowCount > 0) {
//isDeleted = true;
//}else{
//isDeleted = false;
//}
//} catch (Exception e) {
//e.printStackTrace();
//}
//return isDeleted; }
//
//private void rSMaterialButtonCirclelActionPerformed (java.awt.event.ActionEvent evt) {
//if (deleteBook()== true) {
//JOptionPane.showMessageDialog(this, "Book Deleted");
//clearTable();
//setBookDetailsToTable();}
//}
//
//
