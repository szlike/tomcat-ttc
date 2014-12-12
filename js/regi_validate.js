  function validate_form(thisform) {
    with (thisform) {
      if (validate_required(username, "username must be entered") == false) {
        uid.focus();
        return false;
      }
      if (validate_required(password, "password must be entered") == false) {
        pwd.focus();
        return false;
      }
      if (validate_required(first_name, "first name must be entered") == false) {
        first_name.focus();
        return false;
      }
      if (validate_required(last_name, "last name must be entered") == false) {
        last_name.focus();
        return false;
      }
      if (validate_required(email, "email must be entered") == false) {
        email.focus();
        return false;
      }
      if (validate_required(phone, "phone number must be entered") == false) {
        phone.focus();
        return false;
      }
    }
  }
  
  function validate_required(field, alerttxt) {
    with (field) {
      if (value==null||value=="") {
        alert(alerttxt);
        return false;
      }
      else return true;
    }
  }
