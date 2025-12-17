#!/bin/bash

# Variables
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups"
BACKUP_FILE="$BACKUP_DIR/tourflow_backup_$DATE.sql"
RETENTION_DAYS=30

# Création du répertoire de backup s'il n'existe pas
mkdir -p $BACKUP_DIR

# Création du backup
echo "Création du backup de la base de données..."
PGPASSWORD=$POSTGRES_PASSWORD pg_dump -h $POSTGRES_HOST -U $POSTGRES_USER -d $POSTGRES_DB > $BACKUP_FILE

# Compression du backup
echo "Compression du backup..."
gzip $BACKUP_FILE

# Nettoyage des anciens backups
echo "Nettoyage des anciens backups..."
find $BACKUP_DIR -name "tourflow_backup_*.sql.gz" -type f -mtime +$RETENTION_DAYS -delete

# Affichage du statut
if [ $? -eq 0 ]; then
    echo "Backup créé avec succès: $BACKUP_FILE.gz"
else
    echo "Erreur lors de la création du backup"
    exit 1
fi
