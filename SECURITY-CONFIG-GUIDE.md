# Security Configuration Guide - Flight Booking System

## ⚠️ IMPORTANT: Never commit `application.properties` to GitHub!

Your sensitive data (database passwords, API keys, email credentials) should NEVER be in version control.

---

## Setup Instructions

### Option 1: Local Development (Using application.properties)

1. **For each service**, copy the example file:
   ```bash
   # Example for User Service
   cp UserService/src/main/resources/application-example.properties \
      UserService/src/main/resources/application.properties
   ```

2. **Edit each `application.properties`** file with your actual credentials:
   - Database password
   - API keys (Razorpay)
   - Email credentials (Gmail App Password)
   - JWT secret

3. **Never commit this file** (already in .gitignore)

### Option 2: Environment Variables (RECOMMENDED for Production)

Set these environment variables in your system/server:

```bash
# Database (Common for all services)
DB_username=root
DB_password=YOUR_DB_PASSWORD

# User Service
JWT_SECRET=YOUR_SUPER_SECRET_JWT_KEY_MIN_256_BITS

# Payment Service
RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret

# Notification Service
MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

Then update `application.properties` to use environment variables:

```properties
spring.datasource.password=${DB_password}
razorpay.key_id=${RAZORPAY_KEY_ID}
razorpay.key_secret=${RAZORPAY_KEY_SECRET}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

### Option 3: Docker/Kubernetes Secrets (BEST for Production)

Use container orchestration secrets management:
- Docker: Use `--env-file` or `docker secrets`
- Kubernetes: Use `Secrets` objects
- AWS: Use AWS Secrets Manager / Parameter Store

---

## Getting Required Credentials

### Gmail App Password for Email Notifications
1. Go to [Google Account Security](https://myaccount.google.com/)
2. Enable 2-Factor Authentication
3. Go to **App Passwords** (or [direct link](https://myaccount.google.com/apppasswords))
4. Select **Mail** and **Windows Computer**
5. Google will generate a 16-character password
6. Use this in `spring.mail.password`

### Razorpay API Keys
1. Go to [Razorpay Dashboard](https://dashboard.razorpay.com/)
2. Navigate to **Settings** → **API Keys**
3. Copy **Key ID** and **Key Secret** (Test mode for development)
4. Update `razorpay.key_id` and `razorpay.key_secret`

### Database Password
Create a strong MySQL password:
```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourStrongPassword123!@#';
```

---

## Verification Checklist

- ✅ `application.properties` files are in .gitignore
- ✅ No credentials appear in any file that's version controlled
- ✅ Example files (`application-example.properties`) are provided
- ✅ Team members use their own credentials locally
- ✅ Production uses environment variables or secrets manager

---

## If Credentials Were Exposed

**IMMEDIATELY ROTATE ALL CREDENTIALS:**
1. ✅ Change MySQL root password
2. ✅ Revoke Gmail App Password (generate new one)
3. ✅ Regenerate Razorpay API keys in dashboard
4. ✅ Update all deployment configurations
5. ✅ Notify your team

---

## Git Protection

The `.gitignore` now includes:
```
application.properties
application-*.properties
*.env
.env
```

Check that no properties file appears in git status:
```bash
git status
```

All files should be clean! ✅
