using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace LibraryApp.SQLite;

public partial class MasterContext : DbContext
{
    public MasterContext()
    {
    }

    public MasterContext(DbContextOptions<MasterContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Author> Authors { get; set; }

    public virtual DbSet<AuthorBook> AuthorBooks { get; set; }

    public virtual DbSet<Book> Books { get; set; }

    public virtual DbSet<Category> Categories { get; set; }

    public virtual DbSet<CoauthorBook> CoauthorBooks { get; set; }

    public virtual DbSet<DbVer> DbVers { get; set; }

    public virtual DbSet<Version> Versions { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
#warning To protect potentially sensitive information in your connection string, you should move it out of source code. You can avoid scaffolding the connection string by using the Name= syntax to read it from configuration - see https://go.microsoft.com/fwlink/?linkid=2131148. For more guidance on storing connection strings, see http://go.microsoft.com/fwlink/?LinkId=723263.
        => optionsBuilder.UseSqlite("data source=D:\\shamela4_2\\database\\master.db");

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Author>(entity =>
        {
            entity.ToTable("author");

            entity.HasIndex(e => e.DeathNumber, "death_number");

            entity.Property(e => e.AuthorId)
                .ValueGeneratedNever()
                .HasColumnName("author_id");
            entity.Property(e => e.AuthorName).HasColumnName("author_name");
            entity.Property(e => e.DeathNumber).HasColumnName("death_number");
            entity.Property(e => e.DeathText).HasColumnName("death_text");
        });

        modelBuilder.Entity<AuthorBook>(entity =>
        {
            entity
                .HasNoKey()
                .ToTable("author_book");

            entity.HasIndex(e => e.AuthorId, "author_id");

            entity.HasIndex(e => e.BookId, "book_id");

            entity.Property(e => e.AuthorId).HasColumnName("author_id");
            entity.Property(e => e.BookId).HasColumnName("book_id");
        });

        modelBuilder.Entity<Book>(entity =>
        {
            entity.ToTable("book");

            entity.HasIndex(e => e.BookCategory, "book_category");

            entity.HasIndex(e => new { e.BookDate, e.MainAuthor, e.GroupId, e.BookId }, "book_order");

            entity.HasIndex(e => e.BookType, "book_type");

            entity.HasIndex(e => e.CoverOndisk, "cover_ondisk");

            entity.HasIndex(e => e.CoverOnline, "cover_online");

            entity.HasIndex(e => e.GroupId, "group_id");

            entity.HasIndex(e => e.Hidden, "hidden");

            entity.HasIndex(e => e.MajorOndisk, "major_ondisk");

            entity.HasIndex(e => e.MajorOnline, "major_online");

            entity.HasIndex(e => e.MinorOndisk, "minor_ondisk");

            entity.HasIndex(e => e.MinorOnline, "minor_online");

            entity.HasIndex(e => e.Parent, "parent");

            entity.HasIndex(e => e.PdfOnline, "pdf_ondisk");

            entity.HasIndex(e => e.PdfOnline, "pdf_online");

            entity.HasIndex(e => e.Printed, "printed");

            entity.Property(e => e.BookId)
                .ValueGeneratedNever()
                .HasColumnName("book_id");
            entity.Property(e => e.Authors).HasColumnName("authors");
            entity.Property(e => e.BookCategory).HasColumnName("book_category");
            entity.Property(e => e.BookDate).HasColumnName("book_date");
            entity.Property(e => e.BookName).HasColumnName("book_name");
            entity.Property(e => e.BookType).HasColumnName("book_type");
            entity.Property(e => e.CoverOndisk).HasColumnName("cover_ondisk");
            entity.Property(e => e.CoverOnline).HasColumnName("cover_online");
            entity.Property(e => e.GroupId).HasColumnName("group_id");
            entity.Property(e => e.Hidden).HasColumnName("hidden");
            entity.Property(e => e.MainAuthor).HasColumnName("main_author");
            entity.Property(e => e.MajorOndisk).HasColumnName("major_ondisk");
            entity.Property(e => e.MajorOnline).HasColumnName("major_online");
            entity.Property(e => e.MetaData).HasColumnName("meta_data");
            entity.Property(e => e.MinorOndisk).HasColumnName("minor_ondisk");
            entity.Property(e => e.MinorOnline).HasColumnName("minor_online");
            entity.Property(e => e.Parent).HasColumnName("parent");
            entity.Property(e => e.PdfLinks).HasColumnName("pdf_links");
            entity.Property(e => e.PdfOndisk).HasColumnName("pdf_ondisk");
            entity.Property(e => e.PdfOnline).HasColumnName("pdf_online");
            entity.Property(e => e.Printed).HasColumnName("printed");
        });

        modelBuilder.Entity<Category>(entity =>
        {
            entity.ToTable("category");

            entity.HasIndex(e => e.CategoryOrder, "category_order");

            entity.Property(e => e.CategoryId)
                .ValueGeneratedNever()
                .HasColumnName("category_id");
            entity.Property(e => e.CategoryName).HasColumnName("category_name");
            entity.Property(e => e.CategoryOrder).HasColumnName("category_order");
        });

        modelBuilder.Entity<CoauthorBook>(entity =>
        {
            entity
                .HasNoKey()
                .ToTable("coauthor_book");

            entity.HasIndex(e => e.AuthorId, "coauthor_id");

            entity.HasIndex(e => e.BookId, "cobook_id");

            entity.Property(e => e.AuthorId).HasColumnName("author_id");
            entity.Property(e => e.BookId).HasColumnName("book_id");
        });

        modelBuilder.Entity<DbVer>(entity =>
        {
            entity
                .HasNoKey()
                .ToTable("db_ver");

            entity.HasIndex(e => e.Value, "ver").IsUnique();

            entity.Property(e => e.Value).HasColumnName("value");
        });

        modelBuilder.Entity<Version>(entity =>
        {
            entity
                .HasNoKey()
                .ToTable("version");

            entity.HasIndex(e => e.Key, "key").IsUnique();

            entity.Property(e => e.Key).HasColumnName("key");
            entity.Property(e => e.Value).HasColumnName("value");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
