# Kubernetes (k8s) — Beginner's Guide

## Evolution of Deployment Strategies

Before Kubernetes, developers went through several phases of deploying applications:

- **Bare Metal** — Apps ran directly on physical servers. Hard to scale, expensive, and each environment needed manual setup. *(~03:00)*
- **Cloud Native** — Services like AWS made it easier to scale, but developers often got locked into one cloud provider's ecosystem. *(~06:20)*
- **Virtualization** — Virtual machines gave better isolation, but were heavy and slow because each VM ran a full operating system. *(~10:35)*
- **Containerization** — Docker and similar tools made containers that are lightweight and portable, guaranteeing the same behavior across any environment. *(~11:55)*

However, as apps grew to use hundreds or thousands of containers, a new problem emerged: **how do you manage all of them?** This is called **container orchestration**, and it's the problem Kubernetes solves. *(~13:35)*

---

## What is Kubernetes? 

Kubernetes (also written as **k8s**) was inspired by **Google's internal system called Borg**. Google built it as an open-source project and donated it to the **Cloud Native Computing Foundation (CNCF)**.

The name comes from the Greek word for **"helmsman"** — the person who steers a ship. Just like a helmsman guides a vessel, Kubernetes steers your containers.

Its key value: it acts as an **abstraction layer** between your application and the infrastructure, meaning your app doesn't care whether it's running on AWS, GCP, Azure, or on-premises.

---

## Kubernetes Architecture 

A Kubernetes setup is called a **cluster**. A cluster has two types of components: the **Control Plane** (the brain) and **Worker Nodes** (the muscle).

![Kubernetes Architecture](/kubernetes_cluster_architecture.svg)> The diagram above shows a full cluster: the Control Plane on the left, two Worker Nodes in the middle (each running Pods inside a CRI), and the Cloud Provider API on the right connected via the Cloud Controller Manager.

---

### Control Plane — The Brain

The Control Plane makes global decisions about the cluster. It doesn't run your app directly; it manages everything else.

| Component | What it does (in plain English) |
|---|---|
| **API Server** | The front door. Every command you send (via `kubectl` or any tool) goes here first. It validates and processes all requests. |
| **etcd** | The memory. A key-value database that stores the entire state of the cluster. If the cluster crashes, this is what's used to recover. Think of it as the "source of truth." |
| **Scheduler** | The real-estate agent. When a new workload needs to run, the Scheduler finds a Worker Node with enough CPU and RAM to host it. |
| **Controller Manager** | The watchdog. Continuously checks if the cluster is in the state you asked for. If something is wrong, it takes action to fix it. |

---

### Worker Nodes — The Muscle 

Worker Nodes are the machines where your actual application containers run.

| Component | What it does (in plain English) |
|---|---|
| **Kubelet** | The node's personal assistant. Receives instructions from the Control Plane and makes sure the right containers are running and healthy. Reports problems back to the Control Plane. |
| **Kube-proxy** | The traffic director. Manages networking rules so that requests reach the right container, even across different physical machines. |
| **Container Runtime (CRI)** | The engine. The actual software (like *containerd*, *Docker*, or *CRI-O*) that pulls container images and runs them. Kubernetes is flexible — it works with any CRI-compatible runtime. |

---

## The "Desired State" Concept 

This is Kubernetes' superpower. Instead of telling Kubernetes *how* to run your app step by step, you declare *what you want the end result to look like*.

**How it works:**

1. **You write a declaration** — A YAML file saying something like: *"I want 5 copies of this web server running."*
2. **Kubernetes compares** — The Controller Manager checks: "How many are actually running right now?" (say, 2).
3. **Kubernetes acts** — It tells the Scheduler to place 3 more Pods and the Kubelet to start them.
4. **Self-healing** — If one container crashes, the Kubelet notices and reports it. The Controller Manager sees the count dropped to 4 (below the desired 5) and automatically creates a replacement.

> **In short:** You say *what* you want. Kubernetes figures out *how* to get there and keeps it that way.

---

## Cloud Control Manager (CCM) 

When running Kubernetes on a cloud provider, a special component called the **Cloud Control Manager (CCM)** bridges Kubernetes with cloud-specific APIs.

It lets Kubernetes automatically request cloud resources like:
- **Load Balancers** (to distribute traffic)
- **Storage Volumes** (for persistent data)

This keeps your infrastructure definitions consistent and portable, regardless of whether you're on AWS, GCP, or Azure.
