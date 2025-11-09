package com.group3.application.model.bean;

public class UpdateLoyaltyMemberRequest {
    private String fullName;
    private String phone;
    private String email;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UpdateLoyaltyMemberRequest(String fullName, String phone, String email) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    public static class Builder {
        private String fullName;
        private String phone;
        private String email;

        public Builder setFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public String getFullName() {
            return fullName;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }
        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }
        public UpdateLoyaltyMemberRequest build() {
            return new UpdateLoyaltyMemberRequest(fullName, phone, email);
        }
    }
}
